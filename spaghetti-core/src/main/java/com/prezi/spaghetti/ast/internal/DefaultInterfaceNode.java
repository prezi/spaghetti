package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReferenceBase;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.TypeMethodNode;

import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultInterfaceNode extends AbstractParametrizedTypeNode implements InterfaceNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final Set<InterfaceReferenceBase> superInterfaces = new LinkedHashSet<InterfaceReferenceBase>();
	private final NamedNodeSet<TypeMethodNode> methods = new DefaultNamedNodeSet<TypeMethodNode>("method");

	public DefaultInterfaceNode(FQName qualifiedName) {
		super(qualifiedName);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), superInterfaces, methods);
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
	public Set<InterfaceReferenceBase> getSuperInterfaces() {
		return superInterfaces;
	}

	@Override
	public NamedNodeSet<TypeMethodNode> getMethods() {
		return methods;
	}
}
