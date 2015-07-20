package com.prezi.spaghetti.javascript

import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class JavaScriptEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def "generate implicit"() {
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

	def "generate explicit"() {
		def definition = """
enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 2
	@deprecated("escape \\"this\\"!")
	BELA = 5
	GEZA = 1
}
"""
		def result = parseAndVisitEnum(definition, new JavaScriptEnumGeneratorVisitor())

		expect:
		result == """var com;
(function (com) {
	(function(example) {
		(function(test) {
			test.MyEnum = {
				"ALMA": 2,
				"BELA": 5,
				"GEZA": 1
			};
		})(example.test || (example.test = {}));
		var test = example.test;
	})(com.example || (com.example = {}));
	var example = com.example;
})(com || (com = {}));
"""
	}
}
