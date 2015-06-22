package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;

public interface ModuleNode extends NamedNode, DocumentedNode, AnnotatedNode, MethodContainer {
	String getAlias();
	QualifiedNodeSet<ImportNode> getImports();
	QualifiedNodeSet<QualifiedTypeNode> getTypes();
	QualifiedNodeSet<QualifiedTypeNode> getExternTypes();
	ModuleDefinitionSource getSource();
}
