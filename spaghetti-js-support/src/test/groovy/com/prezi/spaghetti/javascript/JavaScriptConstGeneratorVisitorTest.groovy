package com.prezi.spaghetti.javascript

import com.prezi.spaghetti.generator.ConstGeneratorSpecification

class JavaScriptConstGeneratorVisitorTest extends ConstGeneratorSpecification {
	def "generate"() {
		def definition = """
/**
 * My dear constants.
 */
const MyConstants {
	alma: int = 1;
	/**
	 * Bela is -123.
	 */
	bela: int = -123;
	geza = -1.23;
	tibor = "tibor";
}
"""
		def result = parseAndVisitConst(definition, new JavaScriptConstGeneratorVisitor())

		expect:
		result == """var com;
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
})(com || (com = {}));
"""
	}
}
