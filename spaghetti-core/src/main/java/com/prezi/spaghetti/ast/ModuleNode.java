package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;

import java.util.Map;

public interface ModuleNode extends NamedNode, DocumentedNode, AnnotatedNode, MethodContainer {
	String getAlias();
	Map<FQName, ImportNode> getImports();
	QualifiedNodeSet<QualifiedTypeNode> getTypes();
	QualifiedNodeSet<QualifiedTypeNode> getExternTypes();
	ModuleDefinitionSource getSource();
}
