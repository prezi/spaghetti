package com.prezi.spaghetti.haxe.stub

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.TypeReference
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
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def methodDefinitions = visitMethodDefinitions(node, [:], [], Sets.newLinkedHashSet()).join("")

		return   \
  """class ${node.name}Stub${typeParams} implements ${node.name}${typeParams} {
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
			def resolvedMethod = methodNode.resolveWithTypeParameters(bindings)
			methodDefinitions.add resolvedMethod.accept(methodGenerator)
		}

		for (superIfaceRef in interfaceNode.superInterfaces) {
			def superBindings = Maps.newLinkedHashMap(bindings)
			if (superIfaceRef instanceof InterfaceReference) {
				def superIface = ((InterfaceReference) superIfaceRef).type
				for (int i = 0; i < superIface.typeParameters.size(); i++) {
					def param = superIface.typeParameters.getAt(i);
					def ref = superIfaceRef.arguments.get(i);
					superBindings.put(param, ref)
				}
				visitMethodDefinitions(superIface, superBindings, methodDefinitions, methodsGenerated)
			}
		}

		return methodDefinitions
	}

	private static class MethodGenerator extends AbstractHaxeGeneratorVisitor {
		@Override
		String visitMethodNode(MethodNode node) {
			String returnType = node.returnType.accept(this)
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
				if (returnType instanceof PrimitiveTypeReference && returnType.arrayDimensions == 0) {
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
}
