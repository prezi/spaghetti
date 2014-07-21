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
void initModule(int a, int b = 12)
UnicodeString[] doSomething()
@nullable static int doStatic(@nullable int a, int b)
static <T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleAccessorGeneratorVisitor()

		expect:
		visitor.visit(module) == """@:final class Test {

	static var __instance:Dynamic = untyped __js__('SpaghettiConfiguration["__modules"]["com.example.test"]["__instance"]');
	static var __static:Dynamic = untyped __js__('SpaghettiConfiguration["__modules"]["com.example.test"]["__static"]');

	/**
	 * Initializes module.
	 */
	@:deprecated("use doSomething() instead")
	@:extern public inline function initModule(a:Int, b:Int = 12):Void {
		__instance.initModule(a, b);
	}
	@:extern public inline function doSomething():Array<String> {
		return __instance.doSomething();
	}
	@:extern public static inline function doStatic(a:Null<Int>, b:Int):Null<Int> {
		return __static.doStatic(a, b);
	}
	@:extern public static inline function returnT<T>(t:T):com.example.test.MyInterface<T> {
		return __static.returnT(t);
	}

}
"""
	}
}
