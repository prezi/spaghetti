package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.parser.AstParserException
import com.prezi.spaghetti.ast.parser.MissingTypeResolver
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.ast.parser.ModuleTypeResolver
import com.prezi.spaghetti.ast.parser.TypeResolver
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class ModuleConfigurationParser {
	static ModuleConfiguration parse(
			Collection<ModuleDefinitionSource> localModuleSources,
			Collection<ModuleDefinitionSource> dependentModuleSources,
			Collection<ModuleDefinitionSource> transitiveModuleSources) {
		Set<String> parsedModules = []
		def configNode = new DefaultModuleConfiguration()
		def transitiveResolver = parseModules(MissingTypeResolver.INSTANCE, transitiveModuleSources, configNode.transitiveDependentModules, parsedModules)
		def directResolver = parseModules(transitiveResolver, dependentModuleSources, configNode.directDependentModules, parsedModules)
		parseModules(directResolver, localModuleSources, configNode.localModules, parsedModules)
		return configNode
	}

	static TypeResolver parseModules(TypeResolver parentResolver, Collection<ModuleDefinitionSource> sources, Set<ModuleNode> moduleNodes, Set<String> allModuleNames) {
		List<ModuleParser> parsers = sources.collect { ModuleParser.create(it) }
		def resolver = parsers.inject(parentResolver) {
			TypeResolver resolver, parser -> new ModuleTypeResolver(resolver, parser.module)
		}
		parsers.each { parser ->
			def module = parser.parse(resolver)
			if (allModuleNames.contains(module.name)) {
				throw new AstParserException(module.source, ": module loaded multiple times: ${module.name}")
			}
			allModuleNames.add(module.name)
			moduleNodes.add module
		}
		return resolver
	}
}
