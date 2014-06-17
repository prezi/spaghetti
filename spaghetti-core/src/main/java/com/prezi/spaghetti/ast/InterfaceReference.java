package com.prezi.spaghetti.ast;

import java.util.List;

public interface InterfaceReference extends TypeNodeReference<InterfaceNode> {
	List<TypeReference> getArguments();
}
