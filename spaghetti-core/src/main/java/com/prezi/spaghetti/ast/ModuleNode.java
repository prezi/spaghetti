package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;

import java.util.Map;

public interface ModuleNode extends NamedNode, DocumentedNode, AnnotatedNode, MethodContainer<ModuleMethodNode> {
	String getAlias();
	Map<FQName, ImportNode> getImports();
	QualifiedNodeSet<ExternNode> getExterns();
	QualifiedNodeSet<QualifiedTypeNode> getTypes();
	ModuleDefinitionSource getSource();
}
