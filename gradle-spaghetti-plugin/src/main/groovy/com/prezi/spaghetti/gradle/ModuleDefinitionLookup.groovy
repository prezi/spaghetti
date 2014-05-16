package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import org.slf4j.LoggerFactory

/**
 * Created by lptr on 17/11/13.
 */
class ModuleDefinitionLookup {
	private static final LOGGER = LoggerFactory.getLogger(ModuleDefinitionLookup)

	public static List<ModuleBundle> getAllBundles(Iterable<File> files) {
		def bundles = files.collect { File file ->
			LOGGER.debug("Trying to load module bundle from ${file}")
			try {
				def bundle = ModuleBundle.load(file)
				LOGGER.info("Found module bundle ${bundle.name}")
				return bundle
			} catch (ex) {
				LOGGER.debug("Not a module bundle: ${file}: ${ex.getMessage()}")
				LOGGER.trace("Exception", ex)
				return null
			}
		}
		return (bundles - null).sort()
	}

	public static List<ModuleDefinitionSource> getAllDefinitionSources(Iterable<File> configuration) {
		return getAllBundles(configuration).collect { ModuleBundle module ->
			return new ModuleDefinitionSource("module: " + module.name, module.definition)
		}
	}
}
