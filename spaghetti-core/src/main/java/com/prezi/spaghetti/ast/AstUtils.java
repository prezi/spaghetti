package com.prezi.spaghetti.ast;

import com.google.common.collect.Sets;

import java.util.Set;

public class AstUtils {
	public static Set<InterfaceNodeBase> getAllInterfaces(InterfaceNode node) {
		Set<InterfaceNodeBase> allInterfaces = Sets.newLinkedHashSet();
		allInterfaces.add(node);
		addAllInterfaces(node, allInterfaces);
		return allInterfaces;
	}

	private static void addAllInterfaces(InterfaceNode node, Set<InterfaceNodeBase> allInterfaces) {
		for (InterfaceReferenceBase<?> superIfaceRef : node.getSuperInterfaces()) {
			InterfaceNodeBase superIface = superIfaceRef.getType();
			if (allInterfaces.add(superIface)) {
				if (superIface instanceof InterfaceNode) {
					addAllInterfaces((InterfaceNode) superIface, allInterfaces);
				}
			}
		}
	}
}
