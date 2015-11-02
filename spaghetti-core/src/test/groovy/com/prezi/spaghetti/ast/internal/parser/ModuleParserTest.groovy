package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.internal.DefaultFQName

class ModuleParserTest extends AstSpecification {
	def "parse single"() {
		def definition = """module com.example.test {
extern interface JSON;
extern interface Iterable<T>;

enum MyEnum {
	alma,
	bela
}

struct MyStruct<T> {
	en: MyEnum;
	str: JSON;
	value: T;
}

interface Lajos extends Iterable<string> {
}

createStruct(): MyStruct<string>;
}
"""
		def locator = mockLocator(definition)
		def parser = ModuleParser.create(locator.source)

		when:
		def module = parser.parse(mockResolver())

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
		def locatorA = mockLocator("""module test.a {
enum A1 {}
struct A2 {}
interface A3 {}
}
""")
		def locatorB = mockLocator("""module test.b {
import test.a.A1;
import test.a.A2 as AX;

struct MyStruct {
	a1: A1;
	a2: AX;
	a3: test.a.A3;
}
}
""")
		def resolver = mockResolver()
		def moduleA = ModuleParser.create(locatorA.source).parse(resolver)
		resolver = new ModuleTypeResolver(resolver, moduleA)
		def moduleB = ModuleParser.create(locatorB.source).parse(resolver)
		StructNode struct = moduleB.types.get(DefaultFQName.fromString("test.b.MyStruct")) as StructNode

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
