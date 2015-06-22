package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;
import java.util.Map;

public class DefaultMethodNode extends AbstractNamedNode implements MethodNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNodeInternal.NONE;
	private final NamedNodeSetInternal<TypeParameterNode> typeParameters = NodeSets.newNamedNodeSet("type parameter");
	private TypeReference returnType;
	private final NamedNodeSetInternal<MethodParameterNode> parameters = NodeSets.newNamedNodeSet("parameter");

	public DefaultMethodNode(Location location, String name) {
		super(location, name);
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
	public NamedNodeSetInternal<TypeParameterNode> getTypeParameters() {
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
	public NamedNodeSetInternal<MethodParameterNode> getParameters() {
		return parameters;
	}

	static MethodNode resolveWithTypeParameters(MethodNode methodNode, Map<TypeParameterNode, TypeReference> bindings) {
		TypeReference resolvedReturnType = TypeParameterResolver.resolveTypeParameters(methodNode.getReturnType(), bindings);
		DefaultMethodNode resolvedMethod = new DefaultMethodNode(methodNode.getLocation(), methodNode.getName());
		resolvedMethod.setDocumentation(methodNode.getDocumentation());
		resolvedMethod.getAnnotations().addAllInternal(methodNode.getAnnotations());
		resolvedMethod.getTypeParameters().addAllInternal(methodNode.getTypeParameters());
		resolvedMethod.setReturnType(resolvedReturnType);
		for (MethodParameterNode param : methodNode.getParameters()) {
			MethodParameterNode resultParam = DefaultMethodParameterNode.resolveWithTypeParameters(param, bindings);
			resolvedMethod.getParameters().addInternal(resultParam);
		}
		return resolvedMethod;
	}
}
