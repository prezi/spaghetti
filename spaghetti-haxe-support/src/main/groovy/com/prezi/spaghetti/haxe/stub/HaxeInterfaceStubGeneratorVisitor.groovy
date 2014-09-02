package com.prezi.spaghetti.haxe.stub

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.TypeMethodNode
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor

class HaxeInterfaceStubGeneratorVisitor extends AbstractHaxeGeneratorVisitor {
	private static final PRIMITIVE_RETURN_VALUES = [
			(PrimitiveType.BOOL): "false",
			(PrimitiveType.INT): "0",
			(PrimitiveType.FLOAT): "0",
			(PrimitiveType.STRING): "null",
			(PrimitiveType.ANY): "null",
	]

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		def typeName = node.name + "Stub"
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superTypes = node.superInterfaces*.accept(this)
		def methodDefinitions = node.methods*.accept(this).join("")

		return   \
  """${defineType(typeName, superTypes)}
${methodDefinitions}
}
"""
	}

	private static String defineType(String typeName, Collection<String> superTypes) {
		def declaration = "class ${typeName}"
		superTypes.each { superType ->
			declaration += " implements ${superType}"
		}
		return declaration + " {"
	}

	@Override
	String visitTypeMethodNode(TypeMethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		return \
"""	public function ${node.name}${typeParams}(${params}):${returnType} {${createReturnCall(node)}}
"""
	}

	private static String createReturnCall(MethodNode node) {
		String returnCall
		def returnType = node.returnType
		if (returnType == VoidTypeReference.VOID) {
			returnCall = "";
		} else {
			if (returnType instanceof PrimitiveTypeReference) {
				returnCall = "return ${PRIMITIVE_RETURN_VALUES.get(returnType.type)};";
			} else {
				returnCall = "return null;"
			}
			returnCall = "\n\t\t${returnCall}\n\t"
		}
		return returnCall
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		def type = node.type.accept(this)
		type = wrapNullableTypeReference(type, node)
		def result = node.name + ':' + type
		if (node.isOptional()) {
			result = "?" + result
		}
		return result
	}

}
