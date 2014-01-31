package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.ModuleConfigurationParser
import com.prezi.spaghetti.grammar.ModuleParser
import org.gradle.api.artifacts.Configuration
import org.slf4j.LoggerFactory

/**
 * Created by lptr on 17/11/13.
 */
class ModuleDefinitionLookup {
	private static final LOGGER = LoggerFactory.getLogger(ModuleDefinitionLookup)

	public static List<ModuleBundle> getAllBundles(Configuration configuration) {
		LOGGER.debug("Looking for modules in configuration ${configuration.name}")
		return getAllBundles(configuration.files);
	}

	public static List<ModuleBundle> getAllBundles(Collection<File> files) {
		LOGGER.debug("\tLooking at files: ${files.join('\n\t')}")
		def bundles = files.collect { File file ->
			LOGGER.debug("Trying to load module bundle from ${file}")
			try {
				def bundle = ModuleBundle.load(file)
				LOGGER.info("Found module bundle ${bundle.name}")
				return bundle
			} catch (ignore) {
				LOGGER.debug("Not a module bundle: ${file}")
				return null
			}
		}
		return (bundles - null).sort()
	}

	public static List<ModuleParser.ModuleDefinitionContext> getAllDefinitions(Configuration configuration) {
		return getAllBundles(configuration).collect { ModuleBundle module ->
			return ModuleConfigurationParser.parse(module.definition, "module: ${module.name.fullyQualifiedName}")
		}
	}
}
