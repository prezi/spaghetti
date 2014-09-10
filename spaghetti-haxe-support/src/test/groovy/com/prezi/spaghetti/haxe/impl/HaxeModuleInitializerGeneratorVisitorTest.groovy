package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class HaxeModuleInitializerGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test
int doStatic(int x)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleInitializerGeneratorVisitor()

		expect:
		visitor.visit(module) == """@:keep class __TestModuleInit {
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		untyped __haxeModule = new com.example.test.__TestModuleProxy();
		return true;
	}
}
"""
	}
}
