package com.prezi.spaghetti.typescript.type.consts

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.TypeScriptUtils

class TypeScriptDependentConstGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	protected final String foreignModuleName
	protected final ModuleFormat format

	TypeScriptDependentConstGeneratorVisitor(String foreignModuleName, ModuleFormat format) {
		this.foreignModuleName = foreignModuleName
		this.format = format
	}

	@Override
	String visitConstNode(ConstNode node) {
"""export module ${node.name} {
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
			return "\texport const ${node.name}: ${type};\n"
		}
	}
}
