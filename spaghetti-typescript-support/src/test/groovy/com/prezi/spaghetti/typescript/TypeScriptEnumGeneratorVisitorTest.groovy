package com.prezi.spaghetti.typescript

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

	def "generate proxied definition"() {

		def result = parseAndVisitEnum(definition, new TypeScriptDependentEnumGeneratorVisitor("test"))

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
