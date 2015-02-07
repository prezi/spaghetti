package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class TypeScriptModuleInitializerGeneratorVisitorTest extends AstSpecification {
	def "generate"() {
		def definition = """module com.example.test
interface MyInterface<T> {
	/**
	 * This should have nothing to do with the results.
	 */
	void someDummyMethod(int x)
}


int doStatic(int x)
"""
		def module = ModuleParser.create(ModuleDefinitionSource.fromString("test", definition)).parse(mockResolver())
		def visitor = new TypeScriptModuleInitializerGeneratorVisitor()

		expect:
		visitor.visit(module) == """export function __createSpaghettiModule():any {
	return new com.example.test.__TestModuleProxy();
}
"""
	}
}
