package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleParser.ModuleDefinitionContext

/**
 * Created by lptr on 15/11/13.
 */
interface ModuleDefinition extends Scope, Comparable<ModuleDefinition> {
	String getName()
	ModuleType getType()
	String getAlias()
	ModuleDefinitionContext getContext()
	String getDefinitionSource()
	Collection<FQName> getTypeNames()
}
