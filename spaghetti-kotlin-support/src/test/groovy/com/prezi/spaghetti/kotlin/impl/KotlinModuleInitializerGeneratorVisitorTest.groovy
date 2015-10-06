package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class KotlinModuleInitializerGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test
doStatic(x: int): int;
"""
		def result = parseAndVisitModule(definition, new KotlinModuleInitializerGeneratorVisitor())

		expect:
		result == """native public var __kotlinModule:Any = noImpl

fun main(__args:Array<String>) {
	__kotlinModule = __TestModuleProxy()
}
"""
	}
}
