package com.prezi.spaghetti.typescript.type.enums

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor

class TypeScriptDependentEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	protected final String foreignModuleName

	TypeScriptDependentEnumGeneratorVisitor(String foreignModuleName) {
		this.foreignModuleName = foreignModuleName
	}

	@Override
	String visitEnumNode(EnumNode node) {
		"""export enum ${node.name} {
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
			String moduleAccessor = GeneratorUtils.createModuleAccessor(foreignModuleName)
			return "\t${node.name} = ${moduleAccessor}[\"${enumName}\"][\"${node.name}\"]"
		}
	}
}
