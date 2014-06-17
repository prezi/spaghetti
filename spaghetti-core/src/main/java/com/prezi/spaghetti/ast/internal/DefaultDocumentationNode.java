package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.ModuleVisitor;

import java.util.List;

public class DefaultDocumentationNode extends AbstractNode implements DocumentationNode {
	private final List<String> documentation;

	public DefaultDocumentationNode(List<String> documentation) {
		this.documentation = documentation;
	}

	@Override
	public <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitDocumentationNode(this);
	}

	@Override
	public String toString() {
		return "<Documentation>";
	}

	@Override
	public final List<String> getDocumentation() {
		return documentation;
	}
}
