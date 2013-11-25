package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.ModuleConfigurationParser
import com.prezi.spaghetti.grammar.ModuleParser
import org.gradle.api.artifacts.Configuration
/**
 * Created by lptr on 17/11/13.
 */
class ModuleDefinitionLookup {
	public static List<ModuleBundle> getAllBundles(Configuration configuration) {
		def bundles = configuration.files.collect { File file ->
			try {
				return ModuleBundle.load(file)
			} catch (ignore) {
				return null
			}
		}
		return bundles - null
	}

	public static List<ModuleParser.ModuleDefinitionContext> getAllDefinitions(Configuration configuration) {
		return getAllBundles(configuration).collect { ModuleBundle module ->
			return ModuleConfigurationParser.parse(module.definition)
		}
	}
}
