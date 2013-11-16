package com.prezi.gradle.spaghetti

import prezi.spaghetti.SpaghettiModuleParser.ModuleDefinitionContext

/**
 * Created by lptr on 15/11/13.
 */
class ModuleDefinition {
	final FQName name
	final ModuleDefinitionContext context

	ModuleDefinition(FQName name, ModuleDefinitionContext context)
	{
		this.name = name
		this.context = context
	}
}
