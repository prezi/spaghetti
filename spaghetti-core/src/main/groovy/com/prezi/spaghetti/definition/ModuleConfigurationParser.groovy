package com.prezi.spaghetti.definition
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfigurationParser {
	public static ModuleConfiguration parse(
			Collection<ModuleDefinitionSource> localModuleSources,
			Collection<ModuleDefinitionSource> dependentModuleSources,
			Collection<ModuleDefinitionSource> transitiveModuleSources,
			Map<FQName, FQName> externs) {
		def globalScope = new GlobalScope(externs)
		def localModules = parseModules(localModuleSources, globalScope)
		def dependentModules = parseModules(dependentModuleSources, globalScope)
		def transitiveModules = parseModules(transitiveModuleSources, globalScope)
		return new ModuleConfiguration(localModules, dependentModules, transitiveModules, globalScope)
	}

	private static Collection<ModuleDefinition> parseModules(Collection<ModuleDefinitionSource> sources, GlobalScope globalScope)
	{
		return sources.collect { source ->
			def module = new DefaultModuleDefinition(source.contents, ModuleDefinitionParser.parse(source), globalScope)
			globalScope.registerNames(module.typeNames)
			return module
		}
	}
}
