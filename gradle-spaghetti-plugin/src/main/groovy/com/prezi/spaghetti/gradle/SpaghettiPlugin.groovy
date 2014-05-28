package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Platforms
import groovy.io.FileType
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.BinaryContainer
import org.gradle.language.base.ProjectSourceSet
import org.gradle.language.base.internal.BinaryInternal
import org.gradle.language.base.internal.BinaryNamingScheme
import org.gradle.language.base.plugins.LanguageBasePlugin
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	private static final logger = LoggerFactory.getLogger(SpaghettiPlugin)

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

		createPlatformsTask(project)

		def binaryContainer = project.getExtensions().getByType(BinaryContainer)
		def projectSourceSet = project.getExtensions().getByType(ProjectSourceSet)
		def extension = project.extensions.getByType(SpaghettiExtension)

		// Add source sets
		def functionalSourceSet = projectSourceSet.maybeCreate("main")

		def spaghettiSourceSet = instantiator.newInstance(DefaultSpaghettiSourceSet, "spaghetti", functionalSourceSet, fileResolver)
		spaghettiSourceSet.source.srcDir("src/main/spaghetti")
		functionalSourceSet.add(spaghettiSourceSet)

		def spaghettiResourceSet = instantiator.newInstance(DefaultSpaghettiResourceSet, "spaghetti-resources", functionalSourceSet, fileResolver)
		spaghettiResourceSet.source.srcDir("src/main/spaghetti-resources")
		functionalSourceSet.add(spaghettiResourceSet)

		// TODO Use a proper Spaghetti module binary to tie this together
		ProcessSpaghettiResources resourcesTask = project.tasks.create("processSpaghettiResources", ProcessSpaghettiResources)
		resourcesTask.description = "Processes Spaghetti resources"
		resourcesTask.conventionMapping.destinationDir = { project.file("${project.buildDir}/spaghetti/resources") }
		resourcesTask.dependsOn spaghettiResourceSet
		resourcesTask.from(spaghettiResourceSet.source);

		project.tasks.withType(AbstractDefinitionAwareSpaghettiTask).all { AbstractDefinitionAwareSpaghettiTask task ->
			task.conventionMapping.definitions = { findDefinitions(project) }
		}
		project.tasks.withType(AbstractBundleModuleTask).all { AbstractBundleModuleTask task ->
			task.conventionMapping.sourceBaseUrl = { extension.sourceBaseUrl }
			task.conventionMapping.resourcesDirectoryInternal = { resourcesTask.getDestinationDir() }
			task.dependsOn resourcesTask
		}

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

				// TODO Use a proper Spaghetti module binary instead of passing the resourcesTask around
				// Automatically create bundle module task and artifact
				BundleModule bundleTask = createBundleTask(project, binary)
				def zipModule = createZipTask(project, binary, bundleTask, binary.name, "")
				logger.debug("Added bundle task ${bundleTask} with zip task ${zipModule}")
				if (!binary.usedForTesting) {
					project.artifacts.add(extension.configuration.name, zipModule)
					logger.debug("Added bundle artifact for ${binary}")
				}

				// Automatically obfuscate bundle
				ObfuscateModule obfuscateTask = createObfuscateTask(project, binary)
				def zipObfuscated = createZipTask(project, binary, obfuscateTask, binary.name + "-obfuscated", "obfuscated")
				logger.debug("Added obfuscate task ${obfuscateTask} with zip artifact ${zipObfuscated}")
				if (!binary.usedForTesting) {
					project.artifacts.add(extension.obfuscatedConfiguration.name, zipObfuscated)
					logger.debug("Added obfuscated bundle artifact for ${binary}")
				}
			}
		})
	}

	private static void createPlatformsTask(Project project) {
		if (project.tasks.findByName("spaghetti-platforms")) {
			return
		}
		def platformsTask = project.tasks.create("spaghetti-platforms")
		platformsTask.group = "help"
		platformsTask.description = "Show supported Spaghetti platforms."
		platformsTask.doLast {
			def factories = Platforms.generatorFactories
			if (factories.empty) {
				println "No platform support for Spaghetti is found"
			} else {
				println "Spaghetti supports the following platforms:\n"
				def length = factories*.platform.max { a, b -> a.length() <=> b.length() }.length()
				factories.each { factory ->
					println "  " + factory.platform.padRight(length) + " - " + factory.description
				}
			}
		}
	}

	private static BundleModule createBundleTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		BinaryNamingScheme namingScheme = ((BinaryInternal) binary).namingScheme
		def bundleTaskName = namingScheme.getTaskName("bundle")
		def bundleTask = project.tasks.create(bundleTaskName, BundleModule)
		bundleTask.description = "Bundles ${binary} module."
		bundleTask.conventionMapping.inputFile = { binary.getJavaScriptFile() }
		bundleTask.conventionMapping.sourceMap = { binary.getSourceMapFile() }
		bundleTask.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/bundle/${binary.name}") }
		bundleTask.dependsOn binary
		binary.bundleTask = bundleTask
		return bundleTask
	}

	private static ObfuscateModule createObfuscateTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		BinaryNamingScheme namingScheme = ((BinaryInternal) binary).namingScheme
		def obfuscateTaskName = namingScheme.getTaskName("obfuscate")
		def obfuscateTask = project.tasks.create(obfuscateTaskName, ObfuscateModule)
		obfuscateTask.description = "Obfuscates ${binary} module."
		obfuscateTask.conventionMapping.inputFile = { binary.getJavaScriptFile() }
		obfuscateTask.conventionMapping.sourceMap = { binary.getSourceMapFile() }
		obfuscateTask.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/obfuscation/${binary.name}") }
		obfuscateTask.dependsOn binary
		binary.obfuscateTask = obfuscateTask
		return obfuscateTask
	}

	private static Zip createZipTask(Project project, SpaghettiCompatibleJavaScriptBinary binary, AbstractBundleModuleTask bundleTask, String name, String taskName) {
		BinaryNamingScheme namingScheme = ((BinaryInternal) binary).namingScheme
		def zipTaskName = namingScheme.getTaskName("zip", taskName)
		def zipTask = project.tasks.create(zipTaskName, Zip)
		zipTask.description = "Zip up ${name} ${binary}."
		zipTask.dependsOn bundleTask
		zipTask.from { bundleTask.outputDirectory }
		zipTask.conventionMapping.baseName = { name }
		return zipTask
	}

	// TODO Make this into a lazy FileCollection
	static FileCollection findDefinitions(Project project) {
		Set<SpaghettiSourceSet> sources = project.extensions.getByType(ProjectSourceSet).getByName("main").withType(SpaghettiSourceSet)
		Set<File> sourceDirs = sources*.source*.srcDirs.flatten()
		return project.files(sourceDirs.collectMany { File dir ->
			def definitions = []
			if (dir.directory) {
				dir.eachFileMatch(FileType.FILES, ~/^.+\.module$/, { definitions << it })
			}
			return definitions
		})
	}
}
