package com.prezi.spaghetti.javascript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.EnumParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class JavaScriptEnumGeneratorVisitorTest extends AstTestBase {
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
		def context = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", definition)).parser.enumDefinition()
		def parser = new EnumParser(context, "com.example.test")
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
