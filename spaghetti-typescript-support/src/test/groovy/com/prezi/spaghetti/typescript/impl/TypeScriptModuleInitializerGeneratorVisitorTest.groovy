package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptModuleInitializerGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {
	interface MyInterface<T> {
		/**
		 * This should have nothing to do with the results.
		 */
		someDummyMethod(x: int): void;
	}


	doStatic(x: int): int;
}
"""
		def result = parseAndVisitModule(definition, new TypeScriptModuleInitializerGeneratorVisitor(getNamespace()))

		expect:
		result == """export function __createSpaghettiModule():any {
	return new __TestModuleProxy();
}
"""
	}
}
