package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class ModuleParserTest extends AstTestBase {
	def "parse single"() {
		def definition = """module com.example.test
extern interface UnicodeString
extern UnicodeString2

enum MyEnum {
	alma
	bela
}

struct MyStruct {
	MyEnum en
	UnicodeString str
}
"""

		def parser = ModuleParser.create(new ModuleDefinitionSource("test", definition))

		when:
		parser.parse(mockResolver())
		def module = parser.module

		then:
		module.name == "com.example.test"
		module.alias == "Test"
		module.types*.qualifiedName*.toString().asList() == [
				"com.example.test.MyEnum",
				"com.example.test.MyStruct",
		]
		0 * _
	}

	def "parse import"() {
		def moduleADef = """module test.a
enum A1 {}
struct A2 {}
interface A3 {}
"""
		def moduleBDef = """module test.b
import test.a.A1
import test.a.A2 as AX

struct MyStruct {
	A1 a1
	AX a2
	test.a.A3 a3
}
"""
		def resolver = mockResolver()
		def moduleA = ModuleParser.create(new ModuleDefinitionSource("test", moduleADef)).parse(resolver)
		resolver = new ModuleTypeResolver(resolver, moduleA)
		def moduleB = ModuleParser.create(new ModuleDefinitionSource("test", moduleBDef)).parse(resolver)
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
