package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.NamedNode
import com.prezi.spaghetti.ast.StructNode

class KotlinDefinitionIteratorVisitor extends ModuleVisitorBase<Void> {

	private final File outputDirectory
	private final String header
	private final String packageName

	KotlinDefinitionIteratorVisitor(File outputDirectory, String header, String packageName) {
		this.outputDirectory = outputDirectory
		this.header = header
		this.packageName = packageName
	}

	private void createSourceFile(NamedNode node, ModuleVisitorBase<String> visitor) {
		def contents = node.accept(visitor)
		KotlinUtils.createKotlinSourceFile(header, packageName, node.name, outputDirectory, contents)
	}

	@Override
	Void visitInterfaceNode(InterfaceNode node) {
		createSourceFile(node, new KotlinInterfaceGeneratorVisitor())
		return null
	}

	@Override
	Void visitEnumNode(EnumNode node) {
		createSourceFile(node, new KotlinEnumGeneratorVisitor())
		return null
	}

	@Override
	Void visitStructNode(StructNode node) {
		createSourceFile(node, new KotlinStructGeneratorVisitor())
		return null
	}

	@Override
	Void visitConstNode(ConstNode node) {
		createSourceFile(node, new KotlinConstGeneratorVisitor())
		return null
	}
}
