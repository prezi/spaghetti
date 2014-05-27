package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.definition.DefinitionParserHelper
import com.prezi.spaghetti.definition.ModuleDefinition
import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class HaxeModuleInitializerGeneratorVisitorTest extends Specification {
	def "generate"() {
		def mockModule1 = createMockModule("com.example.alma")
		def mockModule2 = createMockModule("com.example.bela")
		def module = new DefinitionParserHelper().parse("""module com.example.test
static int doStatic(int x)
""")
		def visitor = new HaxeModuleInitializerGeneratorVisitor(module, [mockModule1, mockModule2])

		expect:
		visitor.processModule() == """@:keep class __TestInit {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		var dependency0:com.example.alma.Alma = untyped SpaghettiConfiguration["__modules"]["com.example.alma"]["__instance"];
		var dependency1:com.example.bela.Bela = untyped SpaghettiConfiguration["__modules"]["com.example.bela"]["__instance"];
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

	private ModuleDefinition createMockModule(String name) {
		def module = Mock(ModuleDefinition)
		module.name >> name
		module.alias >> name.substring(name.lastIndexOf('.') + 1).capitalize()
		return module
	}
}
