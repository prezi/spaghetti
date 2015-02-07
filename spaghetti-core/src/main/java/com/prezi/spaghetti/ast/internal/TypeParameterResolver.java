package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ExternInterfaceReference;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.StructReference;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Map;

public class TypeParameterResolver {
	public static TypeReferenceInternal resolveTypeParameters(TypeReference node, Map<TypeParameterNode, TypeReference> bindings) {
		if (node instanceof VoidTypeReferenceInternal) {
			return (VoidTypeReferenceInternal) node;
		} else if (node instanceof PrimitiveTypeReferenceInternal) {
			return (PrimitiveTypeReferenceInternal) node;
		} else if (node instanceof EnumReferenceInternal) {
			return (EnumReferenceInternal) node;
		} else if (node instanceof TypeChainInternal) {
			TypeChainInternal typeChain = (TypeChainInternal) node;
			DefaultTypeChain result = new DefaultTypeChain(typeChain.getLocation(), typeChain.getArrayDimensions());
			for (TypeReferenceInternal elem : typeChain.getElementsInternal()) {
				result.getElementsInternal().add(resolveTypeParameters(elem, bindings));
			}
			return result;
		} else if (node instanceof ParametrizedTypeNodeReferenceInternal) {
			ParametrizedTypeNodeReferenceInternal<?> parametrizedRef = (ParametrizedTypeNodeReferenceInternal<?>) node;
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
			for (TypeReferenceInternal argument : parametrizedRef.getArgumentsInternal()) {
				result.getArgumentsInternal().add(resolveTypeParameters(argument, bindings));
			}
			return result;
		} else if (node instanceof TypeParameterReferenceInternal) {
			TypeParameterReferenceInternal paramRef = (TypeParameterReferenceInternal) node;
			if (!bindings.containsKey(paramRef.getType())) {
				return paramRef;
			} else {
				TypeReferenceInternal resolvedReference = resolveTypeParameters(bindings.get(paramRef.getType()), bindings);
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
