package com.prezi.spaghetti

import org.slf4j.LoggerFactory

class Platforms {
	private static final logger = LoggerFactory.getLogger(Platforms)

	private static final Map<String, GeneratorFactory> generatorFactories = initGeneratorFactories()

	private static Map<String, GeneratorFactory> initGeneratorFactories() {
		def genFactories = [:]
		for (factory in ServiceLoader.load(GeneratorFactory)) {
			genFactories.put(factory.platform, factory)
		}

		logger.info "Loaded generators for ${genFactories.keySet()}"

		return genFactories
	}

	public static Set<GeneratorFactory> getGeneratorFactories() {
		return generatorFactories.values().asImmutable()
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

	public static Set<String> getProtectedSymbols(String platform) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.protectedSymbols
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
