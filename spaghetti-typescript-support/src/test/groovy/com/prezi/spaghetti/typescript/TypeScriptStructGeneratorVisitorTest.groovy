package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.StructParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 21/05/14.
 */
class TypeScriptStructGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """/**
 * Hey this is my struct!
 */
struct MyStruct<T> {
	int a
	/**
	 * This is field b.
	 */
	@deprecated("struct")
	string b
	T t
}
"""
		def context = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", definition)).parser.structDefinition()
		def parser = new StructParser(context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new TypeScriptStructGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """/**
 * Hey this is my struct!
 */
export interface MyStruct<T> {
	a: number;
	/**
	 * This is field b.
	 */
	b: string;
	t: T;

}
"""
	}
}
