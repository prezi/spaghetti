package com.prezi.spaghetti.ast;

import java.util.Set;

public interface InterfaceNode extends InterfaceNodeBase, MethodContainer {
	Set<InterfaceReferenceBase<? extends InterfaceNodeBase>> getSuperInterfaces();

	/**
	 * Returns methods declared on this interface, and all its super-interfaces.
	 * The return types and parameter types of the methods from any generic super-interface
	 * are resolved.
	 */
	NamedNodeSet<MethodNode> getAllMethods();
}
