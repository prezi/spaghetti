package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration
import groovy.io.FileType
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.BinaryContainer
import org.gradle.language.base.ProjectSourceSet
import org.gradle.language.base.plugins.LanguageBasePlugin
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	private static final logger = LoggerFactory.getLogger(SpaghettiPlugin)
	private final Map<String, GeneratorFactory> generatorFactories = [:];

	private final Instantiator instantiator
	private final FileResolver fileResolver

	@Inject
	SpaghettiPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}

	@Override
	void apply(Project project) {
		project.plugins.apply(LanguageBasePlugin)
		project.plugins.apply(SpaghettiBasePlugin)

		for (factory in ServiceLoader.load(GeneratorFactory)) {
			generatorFactories.put factory.platform, factory
		}
		logger.info "Loaded generators for ${generatorFactories.keySet()}"
		createPlatformsTask(project)

		def binaryContainer = project.getExtensions().getByType(BinaryContainer)
		def projectSourceSet = project.getExtensions().getByType(ProjectSourceSet)
		def extension = project.extensions.getByType(SpaghettiExtension)

		// Add source set
		def functionalSourceSet = projectSourceSet.maybeCreate("main")
		def spaghettiSourceSet = instantiator.newInstance(DefaultSpaghettiSourceSet, "spaghetti", functionalSourceSet, fileResolver)
		spaghettiSourceSet.source.srcDir("src/main/spaghetti")
		functionalSourceSet.add(spaghettiSourceSet)

		project.tasks.withType(AbstractSpaghettiTask).all(new Action<AbstractSpaghettiTask>() {
			@Override
			void execute(AbstractSpaghettiTask task) {
				logger.debug("Configuring conventions for ${task}")
				def params = extension.params
				task.conventionMapping.platform = { params.platform }
				task.conventionMapping.configuration = { params.configuration }
				task.conventionMapping.obfuscatedConfiguration = { params.obfuscatedConfiguration }
			}
		})

		// Automatically generate module headers
		def generateTask = project.task("generateHeaders", type: GenerateHeaders) {
			description = "Generates Spaghetti headers."
		} as GenerateHeaders
		logger.debug("Created ${generateTask}")

		// Create source set
		def spaghettiGeneratedSourceSet = functionalSourceSet.findByName("spaghetti-generated")
		if (!spaghettiGeneratedSourceSet) {
			spaghettiGeneratedSourceSet = instantiator.newInstance(DefaultSpaghettiGeneratedSourceSet, "spaghetti-generated", functionalSourceSet, fileResolver)
			functionalSourceSet.add(spaghettiGeneratedSourceSet)
			logger.debug("Added ${spaghettiGeneratedSourceSet}")
		}
		spaghettiGeneratedSourceSet.source.srcDir({ generateTask.getOutputDirectory() })
		spaghettiGeneratedSourceSet.builtBy(generateTask)

		binaryContainer.withType(SpaghettiCompatibleJavaScriptBinary).all(new Action<SpaghettiCompatibleJavaScriptBinary>() {
			@Override
			void execute(SpaghettiCompatibleJavaScriptBinary binary) {
				logger.debug("Creating bundle and obfuscation for ${binary}")

				// Automatically create bundle module task and artifact
				BundleModule bundleTask = createBundleTask(project, binary)
				def moduleBundleArtifact = new ModuleBundleArtifact(bundleTask)
				project.artifacts.add(extension.configuration.name, moduleBundleArtifact)
				logger.debug("Added bundle task ${bundleTask} with artifact ${moduleBundleArtifact}")

				// TODO Probably this should be enabled via command line
				// Automatically obfuscate bundle
				ObfuscateBundle obfuscateTask = createObfuscateTask(project, binary, bundleTask)
				def obfuscatedBundleArtifact = new ModuleBundleArtifact(obfuscateTask)
				project.artifacts.add(extension.obfuscatedConfiguration.name, obfuscatedBundleArtifact)
				logger.debug("Added obfuscate task ${obfuscateTask} with artifact ${obfuscatedBundleArtifact}")
			}
		})
	}

	private Task createPlatformsTask(Project project) {
		return project.tasks.create("spaghetti-platforms") {
			group = "help"
			description = "Show supported Spaghetti platforms."
			doLast {
				if (generatorFactories.empty) {
					println "No platform support for Spaghetti is found"
				} else {
					println "Spaghetti supports the following platforms:\n"
					def length = generatorFactories.keySet().max { a, b -> a.length() <=> b.length() }.length()
					generatorFactories.values().each { factory ->
						println "  " + factory.platform.padRight(length) + " - " + factory.description
					}
				}
			}
		}
	}

	private static BundleModule createBundleTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		def bundleTaskName = "bundle" + binary.name.capitalize()
		def bundleTask = project.task(bundleTaskName, type: BundleModule) {
			description = "Bundles ${binary} module."
		} as BundleModule
		bundleTask.conventionMapping.inputFile = { binary.getJavaScriptFile() }
		bundleTask.conventionMapping.sourceMap = { binary.getSourceMapFile() }
		bundleTask.dependsOn binary
		return bundleTask
	}

	private static ObfuscateBundle createObfuscateTask(Project project, SpaghettiCompatibleJavaScriptBinary binary, BundleModule bundleTask) {
		def obfuscateTaskName = "obfuscate" + binary.name.capitalize()
		def obfuscateTask = project.task(obfuscateTaskName, type: ObfuscateBundle) {
			description = "Obfuscates ${binary} module."
		} as ObfuscateBundle
		obfuscateTask.conventionMapping.inputFile = { bundleTask.getOutputFile() }
		obfuscateTask.dependsOn bundleTask
		return obfuscateTask
	}

	// TODO Make this into a FileCollection so it can be lazy
	static Set<File> findDefinitions(Project project) {
		Set<SpaghettiSourceSet> sources = project.extensions.getByType(ProjectSourceSet).getByName("main").withType(SpaghettiSourceSet)
		Set<File> sourceDirs = sources*.source*.srcDirs.flatten()
		return sourceDirs.collectMany { File dir ->
			def definitions = []
			if (dir.directory) {
				dir.eachFileMatch(FileType.FILES, ~/^.+\.module$/, { definitions << it })
			}
			return definitions
		}
	}

	Generator createGeneratorForPlatform(String platform, ModuleConfiguration config) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.createGenerator(config)
	}

	Map<FQName, FQName> getExterns(String platform) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.getExternMapping().collectEntries([:]) { extern, impl ->
			return [FQName.fromString(extern), FQName.fromString(impl)]
		}
	}

	private GeneratorFactory getGeneratorFactory(String platform) {
		def generatorFactory = generatorFactories.get(platform)
		if (generatorFactory == null) {
			throw new IllegalArgumentException("No generator found for platform \"${platform}\". Supported platforms are: "
					+ generatorFactories.keySet().sort().join(", "))
		}
		generatorFactory
	}
}
