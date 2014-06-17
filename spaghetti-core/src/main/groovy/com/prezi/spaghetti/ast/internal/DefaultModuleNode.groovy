package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.ExternNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ImportNode
import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.QualifiedNodeSet
import com.prezi.spaghetti.ast.TypeNode
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class DefaultModuleNode extends AbstractNamedNode implements ModuleNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final String alias
	final Map<FQName, ImportNode> imports = new LinkedHashMap<>()
	final QualifiedNodeSet<ExternNode> externs = new DefaultQualifiedNodeSet<>("extern")
	final QualifiedNodeSet<TypeNode> types = new DefaultQualifiedNodeSet<>("type")
	final NamedNodeSet<ModuleMethodNode> methods = new DefaultNamedNodeSet<>("method")
	final ModuleDefinitionSource source

	DefaultModuleNode(String name, String alias, ModuleDefinitionSource source) {
		super(name)
		this.alias = alias
		this.source = source
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + imports.values() + externs + types + methods
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitModuleNode(this)
	}
}
