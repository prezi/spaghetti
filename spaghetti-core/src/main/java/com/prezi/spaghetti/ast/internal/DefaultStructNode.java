package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.PropertyNode;
import com.prezi.spaghetti.ast.StructReference;

import java.util.Collections;

public class DefaultStructNode extends AbstractParametrizedTypeNode implements StructNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNodeInternal.NONE;
	private StructReference superStruct;
	private final NamedNodeSetInternal<PropertyNode> properties = NodeSets.newNamedNodeSet("property");
	private final NamedNodeSetInternal<MethodNode> methods = NodeSets.newNamedNodeSet("method");

	public DefaultStructNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), superStruct == null ? Collections.<AstNode>emptySet() : Collections.singleton(superStruct), properties, methods);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitStructNode(this);
	}

	@Override
	public NamedNodeSetInternal<AnnotationNode> getAnnotations() {
		return annotations;
	}

	@Override
	public DocumentationNode getDocumentation() {
		return documentation;
	}

	@Override
	public void setDocumentation(DocumentationNode documentation) {
		this.documentation = documentation;
	}

	@Override
	public StructReference getSuperStruct() {
		return superStruct;
	}

	@Override
	public void setSuperStruct(StructReference superStruct) {
		this.superStruct = superStruct;
	}

	@Override
	public NamedNodeSetInternal<PropertyNode> getProperties() {
		return properties;
	}

	@Override
	public NamedNodeSetInternal<MethodNode> getMethods() {
		return methods;
	}
}
