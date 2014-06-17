package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.ExternNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ImportNode;
import com.prezi.spaghetti.ast.ModuleMethodNode;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.QualifiedNodeSet;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultModuleNode extends AbstractNamedNode implements ModuleNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final String alias;
	private final Map<FQName, ImportNode> imports = new LinkedHashMap<FQName, ImportNode>();
	private final QualifiedNodeSet<ExternNode> externs = new DefaultQualifiedNodeSet<ExternNode>("extern");
	private final QualifiedNodeSet<QualifiedTypeNode> types = new DefaultQualifiedNodeSet<QualifiedTypeNode>("type");
	private final NamedNodeSet<ModuleMethodNode> methods = new DefaultNamedNodeSet<ModuleMethodNode>("method");
	private final ModuleDefinitionSource source;

	public DefaultModuleNode(String name, String alias, ModuleDefinitionSource source) {
		super(name);
		this.alias = alias;
		this.source = source;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), imports.values(), externs, types, methods);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitModuleNode(this);
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
	public final String getAlias() {
		return alias;
	}

	@Override
	public Map<FQName, ImportNode> getImports() {
		return imports;
	}

	@Override
	public QualifiedNodeSet<ExternNode> getExterns() {
		return externs;
	}

	@Override
	public QualifiedNodeSet<QualifiedTypeNode> getTypes() {
		return types;
	}

	@Override
	public NamedNodeSet<ModuleMethodNode> getMethods() {
		return methods;
	}

	@Override
	public ModuleDefinitionSource getSource() {
		return source;
	}

}
