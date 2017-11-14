package com.prezi.spaghetti.typescript.type.enums

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor

class TypeScriptDependentEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	protected final String foreignModuleName
	protected final ModuleFormat format

	TypeScriptDependentEnumGeneratorVisitor(String foreignModuleName, ModuleFormat format) {
		this.foreignModuleName = foreignModuleName
		this.format = format
	}

	@Override
	String visitEnumNode(EnumNode node) {
		"""export declare enum ${node.name} {
${node.values*.accept(new EnumValueVisitor(node.name)).join(",\n")}
}
"""
	}

	private class EnumValueVisitor extends AbstractTypeScriptGeneratorVisitor {
		private final String enumName

		EnumValueVisitor(String enumName) {
			this.enumName = enumName
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\t${node.name}"
		}
	}
}
