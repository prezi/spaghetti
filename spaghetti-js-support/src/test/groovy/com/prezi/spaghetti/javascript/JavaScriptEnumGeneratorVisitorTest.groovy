package com.prezi.spaghetti.javascript

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.AstParserSpecification
import com.prezi.spaghetti.ast.internal.parser.EnumParser

class JavaScriptEnumGeneratorVisitorTest extends AstSpecification {
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
		def locator = mockLocator(definition)
		def context = AstParserSpecification.parser(locator).enumDefinition()
		def parser = new EnumParser(locator, context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new JavaScriptEnumGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """var com;
(function (com) {
	(function(example) {
		(function(test) {
			test.MyEnum = {
				"ALMA": 0,
				"BELA": 1,
				"GEZA": 2
			};
		})(example.test || (example.test = {}));
		var test = example.test;
	})(com.example || (com.example = {}));
	var example = com.example;
})(com || (com = {}));
"""
	}
}
