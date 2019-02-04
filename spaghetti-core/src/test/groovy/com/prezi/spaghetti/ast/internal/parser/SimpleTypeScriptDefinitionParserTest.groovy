package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.internal.parser.AstParserException
import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource
import spock.lang.Specification

class SimpleTypeScriptDefinitionParserTest extends Specification {

	def "parse commonjs dts"() {
		def definition = """/// comment

enum MyEnum {
	alma,
	bela
}

declare module submodule {
	interface Test {}
}
export as namespace com_example_test;
"""
		def source = DefaultModuleDefinitionSource.fromStringWithLang("test.d.ts", definition, DefinitionLanguage.TypeScript);

		when:
		def parser = ModuleParser.create(source)
		def module = parser.parse(null)

		then:
		module.name == "com_example_test"
		module.alias == "com_example_test"
		module.types*.qualifiedName*.toString().asList() == []
		module.externTypes*.qualifiedName*.toString().asList() == []
		module.methods*.name == []
	}

	def "parse missing module"() {
		// In TypeScript 'namespace' keyword is equivalent to 'module'
		def definition = """/// comment
class MyClass {

constructor() {

}
}
"""
		def source = DefaultModuleDefinitionSource.fromStringWithLang("test.d.ts", definition, DefinitionLanguage.TypeScript);

		when:
		def parser = ModuleParser.create(source)
		def module = parser.parse(null)

		then:
		def e = thrown(AstParserException)
		e.message.contains("Cannot find module namespace")
	}

	def "parse non-definition .ts module"() {
		def definition = """/// comment

export enum MyEnum {
	alma,
	bela
}

export module submodule {
	interface Test {}
}
"""
		def source = DefaultModuleDefinitionSource.fromStringWithLang("test.ts", definition, DefinitionLanguage.TypeScript);

		when:
		def parser = ModuleParser.create(source, "com_example_test")
		def module = parser.parse(null)

		then:
		module.name == "com_example_test"
		module.alias == "com_example_test"
		module.source.contents == SimpleTypeScriptDefinitionParser.DEFERRED_DTS_CONTENTS
		module.source.location == "test.ts"
	}

	def "parse definition from bundle has right contents"() {
		def definition = """/// comment
export interface A {}
export as namespace com_example_test;
"""
		def source = DefaultModuleDefinitionSource.fromStringWithLang("internal bundle", definition, DefinitionLanguage.TypeScript);

		when:
		def parser = ModuleParser.create(source)
		def module = parser.parse(null)

		then:
		module.name == "com_example_test"
		module.alias == "com_example_test"
		module.source.contents == definition
	}
}
