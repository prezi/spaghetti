package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;

public abstract class AbstractMethodNode extends AbstractNamedNode implements MutableMethodNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final NamedNodeSet<TypeParameterNode> typeParameters = new DefaultNamedNodeSet<TypeParameterNode>("type parameter");
	private TypeReference returnType;
	private final NamedNodeSet<MethodParameterNode> parameters = new DefaultNamedNodeSet<MethodParameterNode>("parameter");

	public AbstractMethodNode(String name) {
		super(name);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), typeParameters, Collections.singleton(returnType), parameters);
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
