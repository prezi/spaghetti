package com.prezi.spaghetti.haxe.type.enums

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor

class HaxeOpaqueEnumGeneratorVisitor extends StringModuleVisitorBase {
	@Override
	String visitEnumNode(EnumNode node) {
		return """abstract ${node.name}(Int) to Int {}"""
	}
}
