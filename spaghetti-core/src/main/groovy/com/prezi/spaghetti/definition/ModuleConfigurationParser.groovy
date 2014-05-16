package com.prezi.spaghetti.definition
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfigurationParser {
	public static ModuleConfiguration parse(
			Collection<ModuleDefinitionSource> dependentModuleSources,
			Collection<ModuleDefinitionSource> localModuleSources,
			Map<FQName, FQName> externs) {
		def globalScope = new GlobalScope(externs)
		def modules = dependentModuleSources.collect { moduleSource ->
			return parseModule(moduleSource, globalScope)
		}
		def localModules = localModuleSources.collect { moduleSource ->
			return parseModule(moduleSource, globalScope)
		}
		return new ModuleConfiguration(modules + localModules, localModules, globalScope)
	}

	private static ModuleDefinition parseModule(ModuleDefinitionSource source, GlobalScope globalScope)
	{
		def module = new ModuleDefinition(source.contents, ModuleDefinitionParser.parse(source), globalScope)
		globalScope.registerNames(module.typeNames)
		return module
	}
}

