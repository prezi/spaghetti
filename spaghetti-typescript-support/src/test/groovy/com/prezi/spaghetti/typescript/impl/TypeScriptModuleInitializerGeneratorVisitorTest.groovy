package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.definition.DefinitionParserHelper
import com.prezi.spaghetti.definition.ModuleDefinition
import spock.lang.Specification

/**
 * Created by lptr on 24/05/14.
 */
class TypeScriptModuleInitializerGeneratorVisitorTest extends Specification {
	def "generate"() {
		def mockModule1 = createMockModule("com.example.alma")
		def mockModule2 = createMockModule("com.example.bela")
		def module = new DefinitionParserHelper().parse("""module com.example.test
static int doStatic(int x)
""")
		def visitor = new TypeScriptModuleInitializerGeneratorVisitor(module, [mockModule1, mockModule2])

		expect:
		visitor.processModule() == """export function __createSpaghettiModule(config:any):any {
	var dependency0:com.example.alma.Alma = config["__modules"]["com.example.alma"]["__instance"];
	var dependency1:com.example.bela.Bela = config["__modules"]["com.example.bela"]["__instance"];
	var module:com.example.test.ITest = new com.example.test.Test(config, dependency0, dependency1);
	var statics = new com.example.test.__TestStatic();
	return {
		__instance: module,
		__static: statics
	}
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
