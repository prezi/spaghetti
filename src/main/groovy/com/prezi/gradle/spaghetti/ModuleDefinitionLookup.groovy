package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 17/11/13.
 */
class ModuleDefinitionLookup {
	public static List<SpaghettiModuleParser.ModuleDefinitionContext> getAllDefinitions(Configuration configuration)
	{
		def definitions = configuration.files.collect { File file ->
			def module
			try
			{
				module = ModuleBundle.load(file)
			}
			catch (e)
			{
				return null
			}
			return ModuleParser.parse(module.definition)
		}
		return definitions - null
	}
}
