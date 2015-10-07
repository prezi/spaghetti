package com.prezi.spaghetti.ast.internal.parser

import com.google.common.collect.Iterables
import com.prezi.spaghetti.ast.AstSpecification

import static com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal.INT

class ExternParserTest extends AstSpecification {
	def "parse"() {
		def locator = mockLocator("extern interface ExternInterface;")
		def context = AstParserSpecification.parser(locator).externInterfaceDefinition()
		def resolver = Mock(TypeResolver)
		def parser = new ExternInterfaceParser(locator, context)

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "ExternInterface"
		0 * _
	}
}
