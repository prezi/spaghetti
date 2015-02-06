package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNodeBase;
import com.prezi.spaghetti.ast.InterfaceReferenceBase;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.ModuleVisitor;

import java.util.Set;

public class DefaultInterfaceNode extends AbstractParametrizedTypeNode implements InterfaceNode, AnnotatedNodeInternal, DocumentedNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final Set<InterfaceReferenceBase<? extends InterfaceNodeBase>> superInterfaces = Sets.newLinkedHashSet();
	private final NamedNodeSetInternal<MethodNode> methods = NodeSets.newNamedNodeSet("method");

	public DefaultInterfaceNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
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
	public Set<InterfaceReferenceBase<? extends InterfaceNodeBase>> getSuperInterfaces() {
		return superInterfaces;
	}

	@Override
	public NamedNodeSetInternal<MethodNode> getMethods() {
		return methods;
	}
}
