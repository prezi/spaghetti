package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.EnumParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser

class KotlinEnumGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	@deprecated("escape \\"this\\"!")
	BELA
	GEZA
}
"""
		def context = ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", definition)).parser.enumDefinition()
		def parser = new EnumParser(context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new KotlinEnumGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """object MyEnum {
	/**
	 * Alma.
	 */
	val ALMA = 0
	[deprecated("escape \\"this\\"!")]
	val BELA = 1
	val GEZA = 2
}
"""
	}
}
