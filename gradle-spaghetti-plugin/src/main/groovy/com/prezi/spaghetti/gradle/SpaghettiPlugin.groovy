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
import org.gradle.language.base.BinaryContainer

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	static final String CONFIGURATION_NAME = "modules"
	static final String OBFUSCATED_CONFIGURATION_NAME = "modulesObf"

	private final Map<String, GeneratorFactory> generatorFactories = [:];

	@Override
	void apply(Project project)
	{
		createPlatformsTask(project)
		for (factory in ServiceLoader.load(GeneratorFactory)) {
			generatorFactories.put factory.platform, factory
		}
		project.logger.info "Loaded generators for ${generatorFactories.keySet()}"

		def defaultConfiguration = project.configurations.findByName(CONFIGURATION_NAME)
		if (defaultConfiguration == null) {
			defaultConfiguration = project.configurations.create(CONFIGURATION_NAME)
		}

		def defaultObfuscatedConfiguration = project.configurations.findByName(OBFUSCATED_CONFIGURATION_NAME)
		if (defaultObfuscatedConfiguration == null) {
			defaultObfuscatedConfiguration = project.configurations.create(OBFUSCATED_CONFIGURATION_NAME);
		}

		def extension = project.extensions.create "spaghetti", SpaghettiExtension, project, defaultConfiguration, defaultObfuscatedConfiguration
		project.tasks.withType(AbstractSpaghettiTask).all(new Action<AbstractSpaghettiTask>() {
			@Override
			void execute(AbstractSpaghettiTask task) {
				def params = extension.params
				task.conventionMapping.platform = { params.platform }
				task.conventionMapping.configuration = { params.configuration }
				task.conventionMapping.definition = { findModuleDefinition(project) }
				task.conventionMapping.obfuscatedConfiguration = { params.obfuscatedConfiguration }
			}
		})

		// Do we define a module in the project?
		if (findModuleDefinition(project)) {
			// Automatically create generateModuleHeaders task
			project.task("generateModuleHeaders", type: GenerateModuleHeaders) {
				description = "Generates Spaghetti module headers."
			}

			def binaryContainer = project.getExtensions().getByType(BinaryContainer.class)
			binaryContainer.withType(SpaghettiCompatibleJavaScriptBinary).all(new Action<SpaghettiCompatibleJavaScriptBinary>() {
				@Override
				void execute(SpaghettiCompatibleJavaScriptBinary binary) {
					// Automatically create bundle module task and artifact
					BundleModule bundleTask = createBundleTask(project, binary)
					def moduleBundleArtifact = new ModuleBundleArtifact(bundleTask)
					project.artifacts.add(extension.configuration.name, moduleBundleArtifact)

					// TODO Probably this should be enabled via command line
					// Automatically obfuscate bundle
					ObfuscateBundle obfuscateTask = createObfuscateTask(project, binary, bundleTask)
					def obfuscatedBundleArtifact = new ModuleBundleArtifact(obfuscateTask)
					project.artifacts.add(extension.obfuscatedConfiguration.name, obfuscatedBundleArtifact)
				}
			})
		}
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

	/**
	 * Try to find the globally defined Spaghetti module.
	 *
	 * <p>It first looks at the Spaghetti configuration of the project. If that's not
	 * specified, it tries to look up a single <code>.module</code> file in the main
	 * Spaghetti source folder.
	 * @return The file found, or <code>null</code> if none or more than one is found.
	 */
	public static File findModuleDefinition(Project project) {
		def definition = project.extensions.getByType(SpaghettiExtension).getDefinition()
		if (definition) {
			return definition
		}
		def definitionRoot = project.file("src/main/spaghetti")
		if (definitionRoot.exists() && definitionRoot.directory) {
			List<File> files = []
			definitionRoot.eachFileMatch FileType.FILES, ~/.*\.module$/, { files << it }
			if (files.size() == 1) {
				return files.first()
			}
		}
		return null
	}

	private static BundleModule createBundleTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		def bundleTaskName = "bundle" + binary.name.capitalize() + "Module"
		def bundleTask = project.task(bundleTaskName, type: BundleModule) {
			description = "Bundles ${binary} module."
		} as BundleModule
		bundleTask.conventionMapping.inputFile = { binary.getJavaScriptFile() }
		bundleTask.conventionMapping.sourceMap = { binary.getSourceMapFile() }
		bundleTask.dependsOn binary
		return bundleTask
	}

	private static ObfuscateBundle createObfuscateTask(Project project, SpaghettiCompatibleJavaScriptBinary binary, BundleModule bundleTask) {
		def obfuscateTaskName = "obfuscate" + binary.name.capitalize() + "Module"
		def obfuscateTask = project.task(obfuscateTaskName, type: ObfuscateBundle) {
			description = "Obfuscates ${binary} module."
		} as ObfuscateBundle
		obfuscateTask.conventionMapping.inputFile = { bundleTask.getOutputFile() }
		obfuscateTask.dependsOn bundleTask
		return obfuscateTask
	}

	Generator createGeneratorForPlatform(String platform, ModuleConfiguration config)
	{
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.createGenerator(config)
	}

	Map<FQName, FQName> getExterns(String platform) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.getExternMapping().collectEntries([:]) { extern, impl ->
			return [ FQName.fromString(extern), FQName.fromString(impl) ]
		}
	}

	private GeneratorFactory getGeneratorFactory(String platform)
	{
		def generatorFactory = generatorFactories.get(platform)
		if (generatorFactory == null)
		{
			throw new IllegalArgumentException("No generator found for platform \"${platform}\". Supported platforms are: "
					+ generatorFactories.keySet().sort().join(", "))
		}
		generatorFactory
	}
}
