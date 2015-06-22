package com.prezi.spaghetti.ast;

import java.util.List;

public interface DocumentationNode extends AstNode {
	List<String> getDocumentation();
}
