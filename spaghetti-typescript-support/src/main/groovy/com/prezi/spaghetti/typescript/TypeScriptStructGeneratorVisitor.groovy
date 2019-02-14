package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode
import groovy.transform.InheritConstructors

@InheritConstructors
class TypeScriptStructGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superStructs = node.superStructs*.accept(this)
		def extensions = superStructs.size() > 0 ? "extends ${superStructs.join(', ')} " : ""
		def members = node.children.findAll({ it instanceof PropertyNode || it instanceof MethodNode })*.accept(this).join("")
"""export interface ${typeName} ${extensions}{
${members}
}
"""
	}

	@Override
	String visitPropertyNode(PropertyNode node) {
		def optional = node.optional ? "?" : ""
"""	${node.name}${optional}: ${node.type.accept(this)};
"""
	}
}
