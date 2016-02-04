package com.prezi.spaghetti.haxe.type.consts

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor
import com.prezi.spaghetti.haxe.HaxeUtils

class HaxeDependentConstGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	protected final String foreignModuleName

	HaxeDependentConstGeneratorVisitor(String foreignModuleName) {
		this.foreignModuleName = foreignModuleName
	}

	@Override
	String visitConstNode(ConstNode node) {
"""@:final class ${node.name} {
${node.entries*.accept(new ConstEntryVisitor(node.name)).join("")}
}
"""
	}

	private class ConstEntryVisitor extends AbstractHaxeGeneratorVisitor {
		private final String constName

		ConstEntryVisitor(String constName) {
			this.constName = constName
		}

		@Override
		String visitConstEntryNode(ConstEntryNode node) {
			String type = PRIMITIVE_TYPES.get(node.type.type)
			String value = HaxeUtils.toPrimitiveString(node.value)
			// TODO [knuton] Enable after migratory period
			// String moduleAccessor = GeneratorUtils.createModuleAccessor(foreignModuleName)
			// String value = "untyped __js__('${moduleAccessor}[\"${constName}\"][\"${node.name}\"]')"
			return "\tpublic static inline var ${node.name}:${type} = ${value};\n"
		}
	}

}
