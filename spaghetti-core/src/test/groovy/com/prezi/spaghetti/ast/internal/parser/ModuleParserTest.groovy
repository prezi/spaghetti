package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.StructNode

class ModuleParserTest extends AstTestBase {
	def "parse single"() {
		def definition = """module com.example.test
extern interface JSON
extern interface Iterable<T>

enum MyEnum {
	alma
	bela
}

struct MyStruct<T> {
	MyEnum en
	JSON str
	T value
}

interface Lajos extends Iterable<string> {
}

MyStruct<string> createStruct()
"""
		def locator = mockLocator(definition)
		def parser = ModuleParser.create(locator.source)

		when:
		parser.parse(mockResolver())
		def module = parser.module

		then:
		module.name == "com.example.test"
		module.alias == "TestModule"
		module.types*.qualifiedName*.toString().asList() == [
				"com.example.test.MyEnum",
				"com.example.test.MyStruct",
				"com.example.test.Lajos",
		]
		module.externTypes*.qualifiedName*.toString().asList() == [
				"JSON",
				"Iterable"
		]
		module.methods*.name == [
		        "createStruct"
		]
		0 * _
	}

	def "parse import"() {
		def locatorA = mockLocator("""module test.a
enum A1 {}
struct A2 {}
interface A3 {}
""")
		def locatorB = mockLocator("""module test.b
import test.a.A1
import test.a.A2 as AX

struct MyStruct {
	A1 a1
	AX a2
	test.a.A3 a3
}
""")
		def resolver = mockResolver()
		def moduleA = ModuleParser.create(locatorA.source).parse(resolver)
		resolver = new ModuleTypeResolver(resolver, moduleA)
		def moduleB = ModuleParser.create(locatorB.source).parse(resolver)
		StructNode struct = moduleB.types.get(FQName.fromString("test.b.MyStruct")) as StructNode

		expect:
		moduleA.types*.qualifiedName*.toString() == [
				"test.a.A1",
				"test.a.A2",
				"test.a.A3",
		]
		struct.properties*.name.asList() == [
		        "a1", "a2", "a3"
		]
		struct.properties*.type*.type*.name.asList() == [
		        "A1", "A2", "A3"
		]
	}
}
