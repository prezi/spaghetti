package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor

import static com.prezi.spaghetti.typescript.TypeScriptJavaScriptBundleProcessor.CREATE_MODULE_FUNCTION

class TypeScriptModuleInitializerGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""export function ${CREATE_MODULE_FUNCTION}():any {
	return new ${node.name}.__${node.alias}Proxy();
}
"""
	}
}
