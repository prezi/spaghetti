package com.prezi.spaghetti.generator;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;

/**
 * Global repository of supported languages and their generator services.
 * <p>Uses {@link java.util.ServiceLoader} to find implementations of
 * {@link com.prezi.spaghetti.generator.GeneratorService} on the classpath.</p>
 */
public class Generators {
	private static final Logger logger = LoggerFactory.getLogger(Generators.class);
	private static Map<Class<? extends com.prezi.spaghetti.generator.GeneratorService>, SortedMap<String, GeneratorService>> services = loadServices();

	private static Map<Class<? extends GeneratorService>, SortedMap<String, GeneratorService>> loadServices() {
		Map<Class<? extends GeneratorService>, SortedMap<String, GeneratorService>> instances = Maps.newLinkedHashMap();
		ImmutableSortedSet.Builder<String> loadedLanguages = ImmutableSortedSet.naturalOrder();
		for (GeneratorService instance : ServiceLoader.load(GeneratorService.class)) {
			if (instance instanceof HeaderGenerator) {
				addServiceAs(HeaderGenerator.class, instance, instances);
			} else if (instance instanceof StubGenerator) {
				addServiceAs(StubGenerator.class, instance, instances);
			} else if (instance instanceof JavaScriptBundleProcessor) {
				addServiceAs(JavaScriptBundleProcessor.class, instance, instances);
			} else {
				logger.error("Unknown service: {}", instance.getClass());
				continue;
			}
			String language = instance.getLanguage();
			loadedLanguages.add(language);
		}
		logger.info("Loaded support for languages: {}", loadedLanguages.build());
		return instances;
	}

	private static void addServiceAs(Class<? extends GeneratorService> type, GeneratorService instance, Map<Class<? extends GeneratorService>, SortedMap<String, GeneratorService>> instances) {
		SortedMap<String, GeneratorService> typedInstances = instances.get(type);
		if (typedInstances == null) {
			typedInstances = Maps.newTreeMap();
			instances.put(type, typedInstances);
		}
		typedInstances.put(instance.getLanguage(), instance);
	}

	public static Set<String> getSupportedLanguages(Class<? extends GeneratorService> type) {
		return getServiceInstances(type).keySet();
	}

	@SuppressWarnings("unchecked")
	public static <T extends GeneratorService> T getService(Class<T> type, String language) {
		GeneratorService instance = getServiceInstances(type).get(language);
		if (instance == null) {
			throw new IllegalArgumentException("No " + getHumanReadableName(type)
					+ " support loaded for language \"" + language + "\". " +
					"Supported languages are: " + Joiner.on(", ").join(getSupportedLanguages(type)) + ".");
		}
		return (T) instance;
	}

	private static <T extends GeneratorService> String getHumanReadableName(Class<T> type) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, type.getSimpleName()).replaceAll("_", " ");
	}

	private static <T extends GeneratorService> Map<String, ? extends GeneratorService> getServiceInstances(Class<T> type) {
		Map<String, ? extends GeneratorService> instances = services.get(type);
		if (instances == null) {
			throw new IllegalArgumentException("Unknown service: " + type);
		}
		return instances;
	}
}
