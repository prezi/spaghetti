package com.prezi.spaghetti.typescript.stub

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.prezi.spaghetti.ast.AstUtils
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.TypeMethodNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.TypeReference
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor

class TypeScriptInterfaceStubGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
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
		def methodDefinitions = visitMethodDefinitions(node, [:], [], Sets.newLinkedHashSet()).join("")

		return  \
 """${defineType(typeName, superTypes)}
${methodDefinitions}
}
"""
	}

	private def visitMethodDefinitions(InterfaceNode interfaceNode, Map<TypeParameterNode, TypeReference> bindings, Collection<String> methodDefinitions, Set<String> methodsGenerated) {
		def methodGenerator = new MethodGenerator()
		for (methodNode in interfaceNode.methods) {
			if (!methodsGenerated.add(methodNode.name)) {
				continue;
			}
			def resolvedMethod = AstUtils.resolveTypeParameters(methodNode, bindings)
			methodDefinitions.add resolvedMethod.accept(methodGenerator)
		}

		for (superIfaceRef in interfaceNode.superInterfaces) {
			def superBindings = Maps.newLinkedHashMap(bindings)
			if (superIfaceRef instanceof InterfaceReference) {
				def superIface = superIfaceRef.type
				for (int i = 0; i < superIface.typeParameters.size(); i++) {
					def param = superIface.typeParameters[i];
					def ref = superIfaceRef.arguments[i];
					superBindings.put(param, ref)
				}
				visitMethodDefinitions(superIface, bindings, methodDefinitions, methodsGenerated)
			}
		}

		return methodDefinitions
	}

	private static String defineType(String typeName, Collection<String> superTypes) {
		def declaration = "export class ${typeName}"
		if (!superTypes.empty) {
			declaration += " implements ${superTypes.join(", ")}"
		}
		declaration += " {"
		return declaration
	}

	private static class MethodGenerator extends AbstractTypeScriptGeneratorVisitor {

		@Override
		String visitTypeMethodNode(TypeMethodNode node) {
			def returnType = node.returnType.accept(this)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
			def params = node.parameters*.accept(this).join(", ")
			return \
"""	${node.name}${typeParams}(${params}):${returnType} {${createReturnCall(node)}}
"""
		}

		@Override
		String visitMethodParameterNode(MethodParameterNode node) {
			return node.name + (node.optional ? "?" : "") + ':' + node.type.accept(this)
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
	}
}