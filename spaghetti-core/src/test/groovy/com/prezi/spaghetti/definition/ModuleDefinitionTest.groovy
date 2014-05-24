package com.prezi.spaghetti.definition

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lptr on 21/05/14.
 */
class ModuleDefinitionTest extends Specification {

	def "parse illegal"() {
		def parserContext = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", """module com.example.test
struct MyStruct {
	int a;
	int b
}
"""))
		parserContext.parser.moduleDefinition()

		expect:
		parserContext.listener.inError
	}

	@Unroll
	def "parse #data"() {
		def definition = new DefinitionParserHelper().parse(data)

		expect:
		definition.name == name
		definition.alias == alias

		where:
		data                              | name               | alias
		"module com.example.test"         | "com.example.test" | "Test"
		"module com.example.test as Test" | "com.example.test" | "Test"
	}

	def "parse types"() {
		def definition = new DefinitionParserHelper().parse( \
"""module com.example.test

interface MyInterface {}
enum MyEnum {}
struct MyStruct {}

// Constants should not appear among local types
// because they cannot be referred to
const MyConstants {}
""")
		expect:
		definition.typeNames*.fullyQualifiedName.sort() == [
				"com.example.test.MyEnum",
				"com.example.test.MyInterface",
				"com.example.test.MyStruct",
		].sort()
	}
}
