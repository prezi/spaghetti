package com.prezi.spaghetti.ast;

import java.util.Set;

public interface InterfaceNode extends InterfaceNodeBase, MethodContainer<InterfaceMethodNode> {
	Set<InterfaceReferenceBase> getSuperInterfaces();
}
