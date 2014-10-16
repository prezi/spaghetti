package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class KotlinModuleInitializerGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test
int doStatic(int x)
"""
		def module = ModuleParser.create(ModuleDefinitionSource.fromString("test", definition)).parse(mockResolver())
		def visitor = new KotlinModuleInitializerGeneratorVisitor()

		expect:
		visitor.visit(module) == """fun main(__args:Array<String>) {
	__kotlinModule = new __TestModuleProxy()
}
"""
	}
}
