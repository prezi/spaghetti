package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class KotlinModuleInitializerGeneratorVisitorTest extends AstSpecification {
	def "generate"() {
		def definition = """module com.example.test
int doStatic(int x)
"""
		def module = ModuleParser.create(ModuleDefinitionSource.fromString("test", definition)).parse(mockResolver())
		def visitor = new KotlinModuleInitializerGeneratorVisitor()

		expect:
		visitor.visit(module) == """native public var __kotlinModule:Any = noImpl

fun main(__args:Array<String>) {
	__kotlinModule = __TestModuleProxy()
}
"""
	}
}
