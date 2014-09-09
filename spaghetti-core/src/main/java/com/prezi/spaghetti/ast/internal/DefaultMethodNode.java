package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.NodeSets;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;

public class DefaultMethodNode extends AbstractNamedNode implements MutableMethodNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final NamedNodeSet<TypeParameterNode> typeParameters = NodeSets.newNamedNodeSet("type parameter");
	private TypeReference returnType;
	private final NamedNodeSet<MethodParameterNode> parameters = NodeSets.newNamedNodeSet("parameter");

	public DefaultMethodNode(String name) {
		super(name);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), typeParameters, Collections.singleton(returnType), parameters);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitMethodNode(this);
	}

	@Override
	public final NamedNodeSet<AnnotationNode> getAnnotations() {
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
	public NamedNodeSet<TypeParameterNode> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public TypeReference getReturnType() {
		return returnType;
	}

	@Override
	public void setReturnType(TypeReference returnType) {
		this.returnType = returnType;
	}

	@Override
	public NamedNodeSet<MethodParameterNode> getParameters() {
		return parameters;
	}
}
