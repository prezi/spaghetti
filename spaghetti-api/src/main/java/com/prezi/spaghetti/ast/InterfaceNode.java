package com.prezi.spaghetti.ast;

public interface InterfaceNode extends InterfaceNodeBase, MethodContainer {
	QualifiedTypeNodeReferenceSet<InterfaceReferenceBase<? extends InterfaceNodeBase>> getSuperInterfaces();

	/**
	 * Returns methods declared on this interface, and all its super-interfaces.
	 * The return types and parameter types of the methods from any generic super-interface
	 * are resolved.
	 */
	NamedNodeSet<MethodNode> getAllMethods();
}
