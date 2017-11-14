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

	def "generate local definition"() {

		def result = parseAndVisitEnum(definition, new TypeScriptEnumGeneratorVisitor())

		expect:
		result == expectedWith("0", "1")
	}

	def "generate proxied definition for UMD format"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor("test", ModuleFormat.UMD))

		expect:
		result == expectedWith(
				"Spaghetti[\"dependencies\"][\"test\"][\"MyEnum\"][\"ALMA\"]",
				"Spaghetti[\"dependencies\"][\"test\"][\"MyEnum\"][\"BELA\"]")
	}

	def "generate proxied definition for wrapperless format"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor("test", ModuleFormat.Wrapperless))

		expect:
		result == expectedWith(
				"Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyEnum\"][\"ALMA\"]",
				"Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyEnum\"][\"BELA\"]")
	}

	private static String expectedWith(String first, String second) {
		return """export enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = ${first},
	BELA = ${second}
}
"""
	}
}
