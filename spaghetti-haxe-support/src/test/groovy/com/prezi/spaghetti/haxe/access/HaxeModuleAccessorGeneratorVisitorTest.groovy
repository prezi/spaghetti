package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class HaxeModuleAccessorGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

extern interface UnicodeString

interface MyInterface<T> {
	/**
	 * This should not influence anything.
	 */
	int add(int a, int b)
}

/**
 * Initializes module.
 */
@deprecated("use doSomething() instead")
void initModule(int a, ?int b)
UnicodeString[] doSomething()
@nullable int doStatic(@nullable int a, int b)
<T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleAccessorGeneratorVisitor(module)

		expect:
		visitor.visit(module) == """@:final class TestModule {

	static var module:Dynamic = untyped __js__('SpaghettiConfiguration["modules"]["com.example.test"]["module"]');

	/**
	 * Initializes module.
	 */
	@:deprecated("use doSomething() instead")
	@:extern public static inline function initModule(a:Int, ?b:Int):Void {
		TestModule.module.initModule(a, b);
	}
	@:extern public static inline function doSomething():Array<String> {
		return TestModule.module.doSomething();
	}
	@:extern public static inline function doStatic(a:Null<Int>, b:Int):Null<Int> {
		return TestModule.module.doStatic(a, b);
	}
	@:extern public static inline function returnT<T>(t:T):com.example.test.MyInterface<T> {
		return TestModule.module.returnT(t);
	}

}
"""
	}
}
