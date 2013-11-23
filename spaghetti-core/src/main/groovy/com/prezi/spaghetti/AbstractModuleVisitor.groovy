package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleBaseVisitor

/**
 * Created by lptr on 20/11/13.
 */
abstract class AbstractModuleVisitor<T> extends ModuleBaseVisitor<T> {
	protected final ModuleDefinition module
	protected final ModuleConfiguration config

	AbstractModuleVisitor(ModuleConfiguration config, ModuleDefinition module) {
		this.module = module
		this.config = config
	}

	public T processModule() {
		return visit(module.context)
	}
}
