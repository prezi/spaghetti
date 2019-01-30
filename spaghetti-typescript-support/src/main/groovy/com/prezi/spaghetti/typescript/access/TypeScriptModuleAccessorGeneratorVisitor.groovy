package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.MODULE

import groovy.transform.InheritConstructors

@InheritConstructors
class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
		return node.methods*.accept(new MethodVisitor(currentNamespace)).join("")
	}

	@InheritConstructors
	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
		@Override
		String getMethodPrefix() {
			return "export function "
		}
	}
}
