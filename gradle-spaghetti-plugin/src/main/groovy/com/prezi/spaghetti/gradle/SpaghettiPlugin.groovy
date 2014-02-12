package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
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
		for (factory in ServiceLoader.load(GeneratorFactory)) {
			generatorFactories.put factory.platform, factory
		}
		project.logger.info "Loaded generators for ${generatorFactories.keySet()}"

		def defaultConfiguration = project.configurations.findByName(CONFIGURATION_NAME)
		if (defaultConfiguration == null) {
			defaultConfiguration = project.configurations.create(CONFIGURATION_NAME)
		}

		if (project.configurations.findByName(OBFUSCATED_CONFIGURATION_NAME) == null) {
			project.configurations.create(OBFUSCATED_CONFIGURATION_NAME);
		}

		def extension = project.extensions.create "spaghetti", SpaghettiExtension, project, defaultConfiguration
		project.tasks.withType(AbstractSpaghettiTask).all(new Action<AbstractSpaghettiTask>() {
			@Override
			void execute(AbstractSpaghettiTask task) {
				def params = extension.params
				task.conventionMapping.platform = { params.platform }
				task.conventionMapping.configuration = { params.configuration }
				task.conventionMapping.definition = { params.definition }
			}
		})

		project.tasks.create("spaghetti-platforms") {
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

		// Automatically create bundle module task and artifact
		def binaryContainer = project.getExtensions().getByType(BinaryContainer.class)
		binaryContainer.withType(SpaghettiCompatibleJavaScriptBinary).all(new Action<SpaghettiCompatibleJavaScriptBinary>() {
			@Override
			void execute(SpaghettiCompatibleJavaScriptBinary binary) {
				// Is this a module?
				if (extension.definition) {
					BundleModule bundleTask = createBundleTask(project, binary)
					def artifact = new ModuleBundleArtifact(bundleTask)
					project.artifacts.add(extension.configuration.name, artifact)
				}
			}
		})
	}

	private static BundleModule createBundleTask(Project project, SpaghettiCompatibleJavaScriptBinary binary) {
		def bundleTaskName = "bundle" + binary.name.capitalize()
		def bundleTask = project.task(bundleTaskName, type: BundleModule) {
			description = "Bundles ${binary} module"
		} as BundleModule
		bundleTask.conventionMapping.inputFile = { binary.javaScriptFile.call() }
		bundleTask.conventionMapping.sourceMap = { binary.sourceMapFile.call() }
		return bundleTask
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
