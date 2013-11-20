package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.SpaghettiModuleBaseVisitor

/**
 * Created by lptr on 20/11/13.
 */
abstract class AbstractModuleVisitor<T> extends SpaghettiModuleBaseVisitor<T> {
	protected final ModuleDefinition module

	AbstractModuleVisitor(ModuleDefinition module) {
		this.module = module
	}

	public T processModule() {
		return visit(module.context)
	}
}
