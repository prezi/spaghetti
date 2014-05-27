package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptEnumGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	BELA
}
""")
		def visitor = new TypeScriptEnumGeneratorVisitor()

		expect:
		visitor.visit(module.context) == """export enum MyEnum {

	/**
	 * Alma.
	 */
	ALMA = 0,
	BELA = 1
}
"""
	}
}
