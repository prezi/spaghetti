package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.NamedNode
import com.prezi.spaghetti.ast.StructNode

/**
 * Created by lptr on 20/11/13.
 */
class HaxeDefinitionIteratorVisitor extends ModuleVisitorBase<Void> {

	private final File outputDirectory
	private final String packageName

	HaxeDefinitionIteratorVisitor(File outputDirectory, String packageName) {
		this.outputDirectory = outputDirectory
		this.packageName = packageName
	}

	private void createSourceFile(NamedNode node, ModuleVisitorBase<String> visitor) {
		def contents = node.accept(visitor)
		HaxeUtils.createHaxeSourceFile(packageName, node.name, outputDirectory, contents)
	}

	@Override
	Void visitInterfaceNode(InterfaceNode node) {
		createSourceFile(node, new HaxeInterfaceGeneratorVisitor())
		return null
	}

	@Override
	Void visitEnumNode(EnumNode node) {
		createSourceFile(node, new HaxeEnumGeneratorVisitor())
		return null
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
