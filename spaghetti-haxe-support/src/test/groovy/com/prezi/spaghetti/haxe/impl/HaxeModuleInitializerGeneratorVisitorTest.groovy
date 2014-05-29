package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 21/05/14.
 */
class HaxeModuleInitializerGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def mockModule1 = createMockModule("com.example.alma")
		def mockModule2 = createMockModule("com.example.bela")

		def definition = """module com.example.test
static int doStatic(int x)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleInitializerGeneratorVisitor([mockModule1, mockModule2])

		expect:
		visitor.visit(module) == """@:keep class __TestInit {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		var dependency0:com.example.alma.Alma = untyped __js__('SpaghettiConfiguration["__modules"]["com.example.alma"]["__instance"]');
		var dependency1:com.example.bela.Bela = untyped __js__('SpaghettiConfiguration["__modules"]["com.example.bela"]["__instance"]');
		var module:com.example.test.ITest = new com.example.test.Test(dependency0, dependency1);
		var statics = new com.example.test.__TestStatic();
		untyped __haxeModule = {
			__instance: module,
			__static: statics
		}
		return true;
	}
#end
}
"""
	}

	private ModuleNode createMockModule(String name) {
		def module = Mock(ModuleNode)
		module.name >> name
		module.alias >> name.substring(name.lastIndexOf('.') + 1).capitalize()
		return module
	}
}
