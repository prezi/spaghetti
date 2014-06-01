package com.prezi.spaghetti.obfuscation

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.ExternNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.StructNode

class SymbolCollectVisitor extends ModuleVisitorBase<Set<String>> {

	@Override
	Set<String> aggregateResult(Set<String> aggregate, Set<String> nextResult) {
		return aggregate + nextResult;
	}

	@Override
	protected Set<String> defaultResult() {
		return [];
	}

	@Override
	Set<String> visitExternNode(ExternNode node) {
		return node.qualifiedName.parts
	}

	@Override
	Set<String> visitInterfaceNode(InterfaceNode node) {
		return node.methods*.name
	}

	@Override
	Set<String> visitStructNode(StructNode node) {
		return node.properties*.name
	}

	@Override
	Set<String> visitConstNode(ConstNode node) {
		return node.entries*.name
	}

	@Override
	Set<String> visitEnumNode(EnumNode node) {
		return node.values*.name
	}

	@Override
	Set<String> visitModuleMethodNode(ModuleMethodNode node) {
		return [node.name]
	}
}
