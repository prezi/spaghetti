package com.prezi.spaghetti.javascript

import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class JavaScriptEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def "generate"() {
		def definition = """
enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	@deprecated("escape \\"this\\"!")
	BELA
	GEZA
}
"""
		def result = parseAndVisitEnum(definition, new JavaScriptEnumGeneratorVisitor())

		expect:
		result == """var com;
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
