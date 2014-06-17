package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.InterfaceMethodNode;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.TypeParameterNode;

import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultInterfaceNode extends AbstractTypeNode implements InterfaceNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final NamedNodeSet<TypeParameterNode> typeParameters = new DefaultNamedNodeSet<TypeParameterNode>("type parameter");
	private final Set<InterfaceReference> superInterfaces = new LinkedHashSet<InterfaceReference>();
	private final NamedNodeSet<InterfaceMethodNode> methods = new DefaultNamedNodeSet<InterfaceMethodNode>("method");

	public DefaultInterfaceNode(FQName qualifiedName) {
		super(qualifiedName);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), typeParameters, superInterfaces, methods);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceNode(this);
	}

	@Override
	public NamedNodeSet<AnnotationNode> getAnnotations() {
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
	public Set<InterfaceReference> getSuperInterfaces() {
		return superInterfaces;
	}

	@Override
	public NamedNodeSet<InterfaceMethodNode> getMethods() {
		return methods;
	}
}
