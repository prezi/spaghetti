package com.prezi.spaghetti.packaging

import spock.lang.Specification
import spock.lang.Unroll

class CommentUtilsTest extends Specification {
	static def TEST_CASES = [
			[
					description: "empty",
					js         : "",
					comment    : "",
					code       : "",
			],
			[
					description: "no comments",
					js         : "rawJs();",
					comment    : "",
					code       : "rawJs();",
			],
			[
					description: "no comments with whitespace",
					js         : "  \n" +
							/*_*/"  rawJs();",
					comment    : "  \n" +
							/*_*/"  ",
					code       : "rawJs();",
			],
			[
					description: "only block comments",
					js         : "/* comment */",
					comment    : "/* comment */",
					code       : "",
			],
			[
					description: "only line comments",
					js         : "// comment",
					comment    : "// comment",
					code       : "",
			],
			[
					description: "single line comment",
					js         : "// line comment\n" +
							/*_*/"rawJs();",
					comment    : "// line comment\n",
					code       : "rawJs();",
			],
			[
					description: "single block comment",
					js         : "/* block comment */" +
							/*)*/"rawJs();",
					comment    : "/* block comment */",
					code       : "rawJs();",
			],
			[
					description: "code between block comments",
					js         : "/* */ rawJs(); /* */\n",
					comment    : "/* */ ",
					code       : "rawJs(); /* */\n",
			],
			[
					description: "line comment inside block comment",
					js         : "/* block comment\n" +
							/*_*/"// line comment\n" +
							/*_*/" */" +
							/*_*/"rawJs();",
					comment    : "/* block comment\n" +
							/*_*/"// line comment\n" +
							/*_*/" */",
					code       : "rawJs();",
			],
			[
					description: "single line comment with added whitespace and trailing comments",
					js         : "\n" +
							/*_*/"\t// line comment\n" +
							/*_*/"  rawJs(); // further comment\n" +
							/*_*/"/* hello */",
					comment    : "\n" +
							/*_*/"\t// line comment\n" +
							/*_*/"  ",
					code       : "rawJs(); // further comment\n" +
							/*_*/"/* hello */",
			],
	]

	@Unroll
	def "test #description"() {
		def result = CommentUtils.splitInitialComments(javaScript)

		expect:
		result.initialComments == comments
		result.actualCode == code

		where:
		description << TEST_CASES*.description
		javaScript << TEST_CASES*.js
		comments << TEST_CASES*.comment
		code << TEST_CASES*.code
	}
}
