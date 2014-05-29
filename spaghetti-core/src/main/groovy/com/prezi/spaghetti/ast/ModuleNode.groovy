package com.prezi.spaghetti.ast

import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 27/05/14.
 */
interface ModuleNode extends NamedNode, AnnotatedNode, DocumentedNode, MethodContainer<ModuleMethodNode> {
	String getAlias()
	Map<FQName, ImportNode> getImports()
	QualifiedNodeSet<ExternNode> getExterns()
	QualifiedNodeSet<TypeNode> getTypes()
	ModuleDefinitionSource getSource()
}
