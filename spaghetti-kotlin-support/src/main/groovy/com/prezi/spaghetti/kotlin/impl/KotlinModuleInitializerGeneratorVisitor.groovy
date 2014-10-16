package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.kotlin.AbstractKotlinGeneratorVisitor

import static com.prezi.spaghetti.kotlin.KotlinGenerator.KOTLIN_MODULE_VAR

class KotlinModuleInitializerGeneratorVisitor extends AbstractKotlinGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""fun main(__args:Array<String>) {
	${KOTLIN_MODULE_VAR} = new __${node.alias}Proxy()
}
"""
	}
}
