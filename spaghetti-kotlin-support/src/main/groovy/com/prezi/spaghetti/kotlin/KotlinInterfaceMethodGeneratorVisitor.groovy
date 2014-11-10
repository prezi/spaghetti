package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceReferenceBase
import com.prezi.spaghetti.ast.MethodNode

class KotlinInterfaceMethodGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {

	private final String interfaceName
	private final Set<InterfaceReferenceBase> superInterfaces

	KotlinInterfaceMethodGeneratorVisitor(String interfaceName, Set<InterfaceReferenceBase> superInterfaces) {
		this.interfaceName = interfaceName
		this.superInterfaces = collect(superInterfaces)
	}

	private Set<InterfaceReferenceBase> collect(Set<InterfaceReferenceBase> superInterfaces) {
		Set<InterfaceReferenceBase> result = new HashSet<>()
		for (InterfaceReferenceBase ifaceRef : superInterfaces) {
			result.add(ifaceRef)
			result.addAll(collect(ifaceRef.type.superInterfaces))
		}
		return result
	}

	@Override
	boolean isOverridden(MethodNode node) {
		// Members of kotlin.Any
		if (node.name == "hashCode" ||
			node.name == "toString")
			return true

		for (InterfaceReferenceBase ifaceRef : superInterfaces) {
			def iface = ifaceRef.type

			if (iface?.methods?.contains(node.name)) {
				return true
			}
		}
		return false
	}
}
