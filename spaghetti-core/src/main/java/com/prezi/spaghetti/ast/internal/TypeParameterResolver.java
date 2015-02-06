package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.EnumReference;
import com.prezi.spaghetti.ast.ExternInterfaceReference;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.ParametrizedTypeNodeReference;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import com.prezi.spaghetti.ast.StructReference;
import com.prezi.spaghetti.ast.TypeChain;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeParameterReference;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.VoidTypeReference;

import java.util.Map;

public class TypeParameterResolver {
	public static TypeReference resolveTypeParameters(TypeReference node, Map<TypeParameterNode, TypeReference> bindings) {
		if (node instanceof VoidTypeReference
				|| node instanceof PrimitiveTypeReference
				|| node instanceof EnumReference) {
			return node;
		} else if (node instanceof TypeChain) {
			TypeChain typeChain = (TypeChain) node;
			DefaultTypeChain result = new DefaultTypeChain(typeChain.getLocation(), typeChain.getArrayDimensions());
			for (TypeReference elem : typeChain.getElements()) {
				result.getElementsInternal().add(resolveTypeParameters(elem, bindings));
			}
			return result;
		} else if (node instanceof ParametrizedTypeNodeReference) {
			ParametrizedTypeNodeReference<?> parametrizedRef = (ParametrizedTypeNodeReference<?>) node;
			if (parametrizedRef.getArguments().isEmpty()) {
				return parametrizedRef;
			}
			ParametrizedTypeNodeReferenceInternal<?> result;
			if (node instanceof StructReference) {
				StructReference structRef = (StructReference) node;
				result = new DefaultStructReference(structRef.getLocation(), structRef.getType(), structRef.getArrayDimensions());
			} else if(node instanceof InterfaceReference) {
				InterfaceReference ifaceRef = (InterfaceReference) node;
				result = new DefaultInterfaceReference(ifaceRef.getLocation(), ifaceRef.getType(), ifaceRef.getArrayDimensions());
			} else if (node instanceof ExternInterfaceReference) {
				ExternInterfaceReference externRef = (ExternInterfaceReference) node;
				result = new DefaultExternInterfaceReference(externRef.getLocation(), externRef.getType(), externRef.getArrayDimensions());
			} else {
				throw new AssertionError("Unknown parametrized type: " + node.getClass());
			}
			for (TypeReference argument : parametrizedRef.getArguments()) {
				result.getArgumentsInternal().add(resolveTypeParameters(argument, bindings));
			}
			return result;
		} else if (node instanceof TypeParameterReference) {
			TypeParameterReference paramRef = (TypeParameterReference) node;
			if (!bindings.containsKey(paramRef.getType())) {
				return paramRef;
			} else {
				TypeReference resolvedReference = resolveTypeParameters(bindings.get(paramRef.getType()), bindings);
				if (paramRef.getArrayDimensions() == 0) {
					return resolvedReference;
				} else {
					return resolvedReference.withAdditionalArrayDimensions(paramRef.getArrayDimensions());
				}
			}
		} else {
			throw new AssertionError("Unknown type: " + node.getClass());
		}
	}
}
