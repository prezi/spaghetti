package com.prezi.spaghetti

import spock.lang.Specification

/**
 * Created by lptr on 05/05/14.
 */
class SymbolCollectVisitorTest extends Specification {
	def "empty module"() {
		def result = visit "module prezi.test.tibor as Tibor"

		expect:
		result == []
	}

	def "interface methods"() {
		def result = visit """module prezi.test.tibor as Tibor
interface Iface {
	int add(int a, int b)
}
"""
		expect:
		result == ["add"]
	}

	def "if same method is found twice, only one symbol should be found"() {
		def result = visit """module prezi.test.tibor as Tibor
interface Iface {
	int add(int a, int b)
	int sub(int a, int b)
}
int add(int a, int b)
"""
		expect:
		result == ["add", "sub"]
	}

	def "struct properties are found"() {
		def result = visit """module prezi.test.tibor as Tibor
struct Struct {
	int alpha
	int beta
}
"""
		expect:
		result == ["alpha", "beta"]
	}

	def "constants are found"() {
		def result = visit """module prezi.test.tibor as Tibor
const Constants {
	int alpha
	int beta
}
"""
		expect:
		result == ["Constants", "alpha", "beta"]
	}

	def "enum values are found"() {
		def result = visit """module prezi.test.tibor as Tibor
enum LeEnum {
	alpha
	beta
}
"""
		expect:
		result == ["alpha", "beta"]
	}

	private static List<String> visit(String what) {
		def moduleDefCtx = ModuleDefinitionParser.parse(new ModuleDefinitionSource("test", what))
		def result = moduleDefCtx.accept(new SymbolCollectVisitor())
		return result.sort()
	}
}
