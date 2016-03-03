package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.MODULE

class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""interface ${node.alias} {
${node.methods*.accept(new MethodVisitor()).join("")}
}
export var ${node.alias}:${node.alias} = ${GeneratorUtils.createModuleAccessor(node.name)};
"""
	}

	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
	}
}
