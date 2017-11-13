package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.MODULE

class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	protected final ModuleFormat format

	TypeScriptModuleAccessorGeneratorVisitor(ModuleFormat format) {
		this.format = format
	}

	@Override
	String visitModuleNode(ModuleNode node) {
"""export interface ${node.alias} {
${node.methods*.accept(new MethodVisitor()).join("")}
}
export var ${node.alias}:${node.alias};
${node.name} = ${GeneratorUtils.createModuleAccessor(node.name, format)};
"""
	}

	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
	}
}
