package com.prezi.spaghetti.typescript.type.enums

import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class TypeScriptEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def definition = """
enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA,
	BELA
}
"""
	def dependentExpected = """export enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA,
	BELA
}
"""
	def localExpected = """export enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 0,
	BELA = 1
}
"""

	def "generate local definition"() {

		def result = parseAndVisitEnum(definition, new TypeScriptEnumGeneratorVisitor(getNamespace()))

		expect:
		result == localExpected
	}

	def "generate proxied definition for UMD format"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor(getNamespace()))

		expect:
		result == dependentExpected
	}

	def "generate proxied definition for wrapperless format"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor(getNamespace()))

		expect:
		result == dependentExpected
	}
}
