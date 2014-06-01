package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 24/05/14.
 */
class TypeScriptModuleStaticProxyGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

interface MyInterface<T> {
	/**
	 * This should have nothing to do with the results.
	 */
	void someDummyMethod(int x)
}
/**
 * Does something.
 */
void doSomething()

string[] doSomethingElse(int a, int b)
/**
 * No JavaDoc should be generated, this is a non-user-visible class.
 */
@deprecated("This should be ignored, too")
static int doSomethingStatic(int x)
static void doSomethingVoid(int x)
<T, U> T[] hello(T t, U y)
static <T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new TypeScriptModuleStaticProxyGeneratorVisitor(module)

		expect:
		visitor.visit(module) == """export class __TestStatic {
	doSomethingStatic(x:number):number {
		return com.example.test.Test.doSomethingStatic(x);
	}
	doSomethingVoid(x:number):void {
		com.example.test.Test.doSomethingVoid(x);
	}
	returnT<T>(t:T):com.example.test.MyInterface<T> {
		return com.example.test.Test.returnT(t);
	}

}
"""
	}
}
