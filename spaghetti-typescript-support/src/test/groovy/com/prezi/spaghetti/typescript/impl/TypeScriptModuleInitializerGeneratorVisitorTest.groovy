package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptModuleInitializerGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test
interface MyInterface<T> {
	/**
	 * This should have nothing to do with the results.
	 */
	void someDummyMethod(int x)
}


int doStatic(int x)
"""
		def result = parseAndVisitModule(definition, new TypeScriptModuleInitializerGeneratorVisitor())

		expect:
		result == """export function __createSpaghettiModule():any {
	return new com.example.test.__TestModuleProxy();
}
"""
	}
}
