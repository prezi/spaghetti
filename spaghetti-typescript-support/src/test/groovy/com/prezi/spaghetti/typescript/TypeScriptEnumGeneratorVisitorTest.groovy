package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class TypeScriptEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def "generate"() {
		def definition = """
enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA,
	BELA
}
"""
		def result = parseAndVisitEnum(definition, new TypeScriptEnumGeneratorVisitor())

		expect:
		result == """export enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 0,
	BELA = 1
}
"""
	}
}
