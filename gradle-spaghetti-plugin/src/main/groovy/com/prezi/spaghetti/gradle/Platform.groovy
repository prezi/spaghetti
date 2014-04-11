package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration
import org.gradle.api.Task
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class Platform {
	private static final logger = LoggerFactory.getLogger(Platform)

	private static final Map<String, GeneratorFactory> generatorFactories = initGeneratorFactories()

	private static Map<String, GeneratorFactory> initGeneratorFactories() {
		def genFactories = [:]
		for (factory in ServiceLoader.load(GeneratorFactory)) {
			genFactories.put(factory.platform, factory)
		}

		logger.info "Loaded generators for ${genFactories.keySet()}"

		return genFactories
	}

	public static Task createPlatformsTask(Project project) {
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

	public static Generator createGeneratorForPlatform(String platform, ModuleConfiguration config) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.createGenerator(config)
	}

	public static Map<FQName, FQName> getExterns(String platform) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.getExternMapping().collectEntries([:]) { extern, impl ->
			return [FQName.fromString(extern), FQName.fromString(impl)]
		}
	}

	private static GeneratorFactory getGeneratorFactory(String platform) {
		def generatorFactory = generatorFactories.get(platform)
		if (generatorFactory == null) {
			throw new IllegalArgumentException("No generator found for platform \"${platform}\". Supported platforms are: "
					+ generatorFactories.keySet().sort().join(", "))
		}
		generatorFactory
	}
}
