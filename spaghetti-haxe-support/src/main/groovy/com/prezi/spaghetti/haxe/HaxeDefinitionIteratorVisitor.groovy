package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.NamedNode
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.haxe.type.enums.HaxeEnumGeneratorVisitor

class HaxeDefinitionIteratorVisitor extends ModuleVisitorBase<Void> {

	private final File outputDirectory
	private final String header
	private final String packageName

	HaxeDefinitionIteratorVisitor(File outputDirectory, String header, String packageName) {
		this.outputDirectory = outputDirectory
		this.header = header
		this.packageName = packageName
	}

	private void createSourceFile(NamedNode node, ModuleVisitorBase<String> visitor) {
		def contents = node.accept(visitor)
		HaxeUtils.createHaxeSourceFile(header, packageName, node.name, outputDirectory, contents)
	}

	@Override
	Void visitInterfaceNode(InterfaceNode node) {
		createSourceFile(node, new HaxeInterfaceGeneratorVisitor())
		return null
	}

	@Override
	Void visitEnumNode(EnumNode node) {
		createSourceFile(node, createHaxeEnumGeneratorVisitor())
		return null
	}

	ModuleVisitorBase<String> createHaxeEnumGeneratorVisitor() {
		return new HaxeEnumGeneratorVisitor()
	}

	@Override
	Void visitStructNode(StructNode node) {
		createSourceFile(node, new HaxeStructGeneratorVisitor())
		return null
	}

	@Override
	Void visitConstNode(ConstNode node) {
		createSourceFile(node, new HaxeConstGeneratorVisitor())
		return null
	}
}
