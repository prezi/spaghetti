package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ImportNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.QualifiedTypeNode;

import java.util.Collections;

public class DefaultModuleNode extends AbstractNamedNode implements ModuleNodeInternal {
	private static NodeSetKeyExtractor<FQName, ImportNode> IMPORT_QUALIFIED_ALIAS_EXTRACTOR = new NodeSetKeyExtractor<FQName, ImportNode>() {
		@Override
		public FQName key(ImportNode node) {
			return DefaultFQName.fromString(null, node.getAlias());
		}
	};

	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNodeInternal.NONE;
	private final String alias;
	private final QualifiedNodeSetInternal<ImportNode> imports = new DefaultQualifiedNodeSet<ImportNode>(IMPORT_QUALIFIED_ALIAS_EXTRACTOR, "import", Collections.<ImportNode>emptySet());
	private final QualifiedNodeSetInternal<QualifiedTypeNode> types = NodeSets.newQualifiedNodeSet("type");
	private final QualifiedNodeSetInternal<QualifiedTypeNode> externTypes = NodeSets.newQualifiedNodeSet("externType");
	private final NamedNodeSetInternal<MethodNode> methods = NodeSets.newNamedNodeSet("method");

	public DefaultModuleNode(Location location, String name, String alias) {
		super(location, name);
		this.alias = alias;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), imports, types, methods);
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
	public QualifiedNodeSetInternal<ImportNode> getImports() {
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
