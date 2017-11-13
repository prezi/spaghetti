package com.prezi.spaghetti.typescript.type.enums

import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.EnumGeneratorSpecification
import com.prezi.spaghetti.typescript.type.enums.TypeScriptDependentEnumGeneratorVisitor
import com.prezi.spaghetti.typescript.type.enums.TypeScriptEnumGeneratorVisitor

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
	def dependentExpected = """export declare enum MyEnum {
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

		def result = parseAndVisitEnum(definition, new TypeScriptEnumGeneratorVisitor())

		expect:
		result == localExpected
	}

	def "generate proxied definition for UMD format"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor("test", ModuleFormat.UMD))

		expect:
		result == dependentExpected
	}

	def "generate proxied definition for wrapperless format"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor("test", ModuleFormat.Wrapperless))

		expect:
		result == dependentExpected
	}
}
