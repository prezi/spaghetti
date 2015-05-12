package com.prezi.spaghetti.ast;

public interface ModuleNode extends NamedNode, DocumentedNode, AnnotatedNode, MethodContainer {
	String getAlias();
	QualifiedNodeSet<ImportNode> getImports();
	QualifiedNodeSet<QualifiedTypeNode> getTypes();
	QualifiedNodeSet<QualifiedTypeNode> getExternTypes();
	ModuleDefinitionSource getSource();
}
