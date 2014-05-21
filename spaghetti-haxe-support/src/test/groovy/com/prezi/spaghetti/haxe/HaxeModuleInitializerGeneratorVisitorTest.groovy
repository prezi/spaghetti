package com.prezi.spaghetti.haxe

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
		def module = new DefinitionParserHelper().parse("module com.example.test")
		def visitor = new HaxeModuleInitializerGeneratorVisitor(module, [mockModule1, mockModule2])

		expect:
		visitor.processModule() == """@:keep class __TestInit {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		var dependency0:com.example.alma.null = untyped __config["__modules"]["com.example.alma"]["__module"];
		var dependency1:com.example.bela.null = untyped __config["__modules"]["com.example.bela"]["__module"];
		var module:com.example.test.ITest = new com.example.test.Test(untyped __config, dependency0, dependency1);
		untyped __haxeModule = {
			__module: module
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
		return module
	}
}
