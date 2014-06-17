package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class TypeScriptModuleInitializerGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def mockModule1 = createMockModule("com.example.alma")
		def mockModule2 = createMockModule("com.example.bela")
		def definition = """module com.example.test
interface MyInterface<T> {
	/**
	 * This should have nothing to do with the results.
	 */
	void someDummyMethod(int x)
}


static int doStatic(int x)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new TypeScriptModuleInitializerGeneratorVisitor([mockModule1, mockModule2])

		expect:
		visitor.visit(module) == """export function __createSpaghettiModule():any {
	var dependency0:com.example.alma.Alma = SpaghettiConfiguration["__modules"]["com.example.alma"]["__instance"];
	var dependency1:com.example.bela.Bela = SpaghettiConfiguration["__modules"]["com.example.bela"]["__instance"];
	var module:com.example.test.ITest = new com.example.test.Test(dependency0, dependency1);
	var statics = new com.example.test.__TestStatic();
	return {
		__instance: module,
		__static: statics
	}
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
