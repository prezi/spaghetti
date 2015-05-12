package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

import java.util.Collections;
import java.util.List;

public class DefaultDocumentationNode extends AbstractNode implements DocumentationNodeInternal {
	private final List<String> documentation;

	public DefaultDocumentationNode(Location location, List<String> documentation) {
		super(location);
		this.documentation = Collections.unmodifiableList(documentation);
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
