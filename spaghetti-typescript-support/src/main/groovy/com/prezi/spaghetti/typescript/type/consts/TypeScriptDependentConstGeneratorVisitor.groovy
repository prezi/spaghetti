package com.prezi.spaghetti.typescript.type.consts

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.TypeScriptUtils

class TypeScriptDependentConstGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	protected final String foreignModuleName

	TypeScriptDependentConstGeneratorVisitor(String foreignModuleName) {
		this.foreignModuleName = foreignModuleName
	}

	@Override
	String visitConstNode(ConstNode node) {
"""export class ${node.name} {
${node.entries*.accept(new ConstEntryVisitor(node.name)).join("")}
}
"""
	}

	private class ConstEntryVisitor extends AbstractTypeScriptGeneratorVisitor {
		private final String constName

		ConstEntryVisitor(String constName) {
			this.constName = constName
		}

		@Override
		String visitConstEntryNode(ConstEntryNode node) {
			String type = PRIMITIVE_TYPES.get(node.type.type)
			String value = TypeScriptUtils.toPrimitiveString(node.value)
			// TODO [knuton] Enable after migratory period
			// String moduleAccessor = GeneratorUtils.createModuleAccessor(foreignModuleName)
			// String value = "${moduleAccessor}[\"${constName}\"][\"${node.name}\"]"
			return "\tstatic ${node.name}: ${type} = ${value};\n"
		}
	}
}
