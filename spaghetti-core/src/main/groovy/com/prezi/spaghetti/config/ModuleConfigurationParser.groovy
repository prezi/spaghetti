package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.parser.MissingTypeResolver
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.ast.parser.ModuleTypeResolver
import com.prezi.spaghetti.ast.parser.TypeResolver
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 30/05/14.
 */
class ModuleConfigurationParser {
	static ModuleConfiguration parse(
			Collection<ModuleDefinitionSource> localModuleSources,
			Collection<ModuleDefinitionSource> dependentModuleSources,
			Collection<ModuleDefinitionSource> transitiveModuleSources) {
		def configNode = new DefaultModuleConfiguration()
		def transitiveResolver = resolveModules(MissingTypeResolver.INSTANCE, transitiveModuleSources, configNode.transitiveDependentModules)
		def directResolver = resolveModules(transitiveResolver, dependentModuleSources, configNode.directDependentModules)
		resolveModules(directResolver, localModuleSources, configNode.localModules)
		return configNode
	}

	static TypeResolver resolveModules(TypeResolver parentResolver, Collection<ModuleDefinitionSource> sources, Set<ModuleNode> moduleNodes) {
		List<ModuleParser> parsers = sources.collect { ModuleParser.create(it) }
		def resolver = parsers.inject(parentResolver) {
			TypeResolver resolver, parser -> new ModuleTypeResolver(resolver, parser.module)
		}
		moduleNodes.addAll parsers*.parse(resolver)
		return resolver
	}
}
