package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase

import static com.prezi.spaghetti.haxe.HaxeJavaScriptBundleProcessor.HAXE_MODULE_VAR

class HaxeModuleInitializerGeneratorVisitor extends StringModuleVisitorBase {

	@Override
	String visitModuleNode(ModuleNode node) {
		def initializerName = "__" + node.alias + "Init"

		def initializerContents =
"""@:keep class ${initializerName} {
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		untyped ${HAXE_MODULE_VAR} = new ${node.name}.__${node.alias}Proxy();
		return true;
	}
}
"""
		return initializerContents
	}
}
