package com.prezi.spaghetti.typescript.type.consts

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.TypeScriptUtils
import groovy.transform.InheritConstructors

@InheritConstructors
class TypeScriptDependentConstGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitConstNode(ConstNode node) {
"""export module ${node.name} {
${node.entries*.accept(new ConstEntryVisitor(node.name)).join("")}
}
"""
	}

	@InheritConstructors
	private class ConstEntryVisitor extends AbstractTypeScriptGeneratorVisitor {
		@Override
		String visitConstEntryNode(ConstEntryNode node) {
			String type = PRIMITIVE_TYPES.get(node.type.type)
			return "\texport const ${node.name}: ${type};\n"
		}
	}
}
