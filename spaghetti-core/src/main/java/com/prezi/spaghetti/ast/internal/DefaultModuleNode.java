package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ImportNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;

import java.util.Map;

public class DefaultModuleNode extends AbstractNamedNode implements ModuleNode, AnnotatedNodeInternal, DocumentedNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final String alias;
	private final Map<FQName, ImportNode> imports = Maps.newLinkedHashMap();
	private final QualifiedNodeSetInternal<QualifiedTypeNode> types = NodeSets.newQualifiedNodeSet("type");
	private final QualifiedNodeSetInternal<QualifiedTypeNode> externTypes = NodeSets.newQualifiedNodeSet("externType");
	private final NamedNodeSetInternal<MethodNode> methods = NodeSets.newNamedNodeSet("method");

	public DefaultModuleNode(Location location, String name, String alias) {
		super(location, name);
		this.alias = alias;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), imports.values(), types, methods);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitModuleNode(this);
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
	public final String getAlias() {
		return alias;
	}

	@Override
	public Map<FQName, ImportNode> getImports() {
		return imports;
	}

	@Override
	public QualifiedNodeSetInternal<QualifiedTypeNode> getTypes() {
		return types;
	}

	@Override
	public QualifiedNodeSetInternal<QualifiedTypeNode> getExternTypes() {
		return externTypes;
	}

	@Override
	public NamedNodeSetInternal<MethodNode> getMethods() {
		return methods;
	}

	@Override
	public ModuleDefinitionSource getSource() {
		return getLocation().getSource();
	}

}
