package com.prezi.spaghetti.obfuscation

import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.ast.internal.parser.TypeResolver
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource
import com.prezi.spaghetti.obfuscation.internal.SymbolCollectVisitor
import spock.lang.Specification

class SymbolCollectVisitorTest extends Specification {
	def "empty module"() {
		def result = visit "module prezi.test.tibor as Tibor {}"

		expect:
		result == []
	}

	def "interface methods"() {
		def result = visit """module prezi.test.tibor as Tibor {
	interface Iface {
		add(a: int, b: int): int;
	}
}
"""
		expect:
		result == ["add"]
	}

	def "if same method is found twice, only one symbol should be found"() {
		def result = visit """module prezi.test.tibor as Tibor {
	interface Iface {
		add(a: int, b: int): int;
		sub(a: int, b: int): int;
	}
	add(a: int, b: int): int;
}
"""
		expect:
		result == ["add", "sub"]
	}

	def "struct properties and methods are found"() {
		def result = visit """module prezi.test.tibor as Tibor {
	struct Struct {
		alpha: int;
		beta: int;
		add(a: int, b: int): int;
	}
}
"""
		expect:
		result == ["add", "alpha", "beta"]
	}

	def "constants are found"() {
		def result = visit """module prezi.test.tibor as Tibor {
	const Constants {
		alpha: int = 1;
		beta = 5.0;
	}
}
"""
		expect:
		result == ["alpha", "beta"]
	}

	def "enum values are found"() {
		def result = visit """module prezi.test.tibor as Tibor {
	enum LeEnum {
		alpha,
		beta
	}
}
"""
		expect:
		result == ["LeEnum", "alpha", "beta"]
	}

	private List<String> visit(String what) {
		def module = ModuleParser.create(DefaultModuleDefinitionSource.fromString("test", what)).parse(Mock(TypeResolver))
		def result = module.accept(new SymbolCollectVisitor())
		return result.sort()
	}
}
