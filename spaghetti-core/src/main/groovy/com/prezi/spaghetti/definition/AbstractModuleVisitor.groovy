package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleBaseVisitor

/**
 * Created by lptr on 20/11/13.
 */
abstract class AbstractModuleVisitor<T> extends ModuleBaseVisitor<T> {
	protected final ModuleDefinition module

	AbstractModuleVisitor(ModuleDefinition module) {
		this.module = module
	}

	public T processModule() {
		return visit(module.context)
	}
}
