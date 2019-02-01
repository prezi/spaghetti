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
		def methods = node.methods*.accept(new MethodVisitor(currentNamespace)).join("")
		return "export module ${node.alias} {\n${methods}}\n"
	}

	@InheritConstructors
	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
		@Override
		String getMethodPrefix() {
			return "\texport function "
		}
	}
}
