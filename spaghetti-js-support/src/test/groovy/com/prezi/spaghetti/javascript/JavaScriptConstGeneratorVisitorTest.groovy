package com.prezi.spaghetti.javascript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class JavaScriptConstGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

/**
 * My dear constants.
 */
const MyConstants {
	int alma = 1
	/**
	 * Bela is -123.
	 */
	int bela = -123
	geza = -1.23
	tibor = "tibor"
}
"""
		def parser = ModuleParser.create(new ModuleDefinitionSource("test", definition))
		def module = parser.parse(mockResolver())
		def visitor = new JavaScriptConstGeneratorVisitor()

		expect:
		visitor.visit(module) == """var com;
(function (com) {
	(function(example) {
		(function(test) {
			test.MyConstants = {
				"alma": 1,
				"bela": -123,
				"geza": -1.23,
				"tibor": "tibor"
			};
		})(example.test || (example.test = {}));
		var test = example.test;
	})(com.example || (com.example = {}));
	var example = com.example;
})(com || com = {}));
"""
	}
}
