package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 17/11/13.
 */
class ModuleDefinitionLookup {
	public static List<ModuleBundle> getAllBundles(Configuration configuration) {
		def bundles = configuration.files.collect { File file ->
			try {
				return ModuleBundle.load(file)
			} catch (e) {
				return null
			}
		}
		return bundles - null
	}

	public static List<SpaghettiModuleParser.ModuleDefinitionContext> getAllDefinitions(Configuration configuration) {
		return getAllBundles(configuration).collect { ModuleBundle module ->
			return ModuleParser.parse(module.definition)
		}
	}
}
