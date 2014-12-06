package com.prezi.spaghetti.ast;

import java.util.Set;

public interface InterfaceNode extends InterfaceNodeBase, MethodContainer {
	Set<InterfaceReferenceBase<? extends InterfaceNodeBase>> getSuperInterfaces();
}
