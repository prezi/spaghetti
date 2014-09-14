package com.prezi.spaghetti.generator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.internal.DefaultGeneratorParameters;
import com.prezi.spaghetti.internal.Version;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Global repository of supported languages and their generator factories.
 * <p>Uses {@link java.util.ServiceLoader} to find implementations of
 * {@link com.prezi.spaghetti.generator.GeneratorFactory} on the classpath.</p>
 */
public class Languages {
	private static final Logger logger = LoggerFactory.getLogger(Languages.class);
	private static final Map<String, GeneratorFactory> generatorFactories = initGeneratorFactories();

	private static Map<String, GeneratorFactory> initGeneratorFactories() {
		LinkedHashMap<String, GeneratorFactory> genFactories = Maps.newLinkedHashMap();
		for (GeneratorFactory factory : ServiceLoader.load(GeneratorFactory.class)) {
			genFactories.put(factory.getLanguage(), factory);
		}

		logger.info("Loaded generators for " + String.valueOf(genFactories.keySet()));

		return genFactories;
	}

	/**
	 * Returns a set of available generator factories.
	 */
	public static Set<GeneratorFactory> getGeneratorFactories() {
		return ImmutableSet.copyOf(generatorFactories.values());
	}

	/**
	 * Creates a generator for a given language.
	 *
	 * @param language the target language for the generator.
	 * @param config   the module configuration to use with the generator.
	 */
	public static Generator createGeneratorForLanguage(String language, ModuleConfiguration config) {
		GeneratorFactory generatorFactory = getGeneratorFactory(language);
		return generatorFactory.createGenerator(
				new DefaultGeneratorParameters(config, createHeader())
		);
	}

	/**
	 * Returns the protected symbols of a given language. These symbols should be protected during obfuscation.
	 * @param language the language.
	 */
	public static Set<String> getProtectedSymbols(String language) {
		GeneratorFactory generatorFactory = getGeneratorFactory(language);
		return generatorFactory.getProtectedSymbols();
	}

	private static String createHeader() {
		return "Generated by Spaghetti " + Version.SPAGHETTI_VERSION + " at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	private static GeneratorFactory getGeneratorFactory(final String language) {
		GeneratorFactory generatorFactory = generatorFactories.get(language);
		if (generatorFactory == null) {
			throw new IllegalArgumentException("No generator found for language \"" + language + "\". Supported languages are: " + StringUtils.join(generatorFactories.keySet(), ", "));
		}

		return generatorFactory;
	}
}
