package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class HaxeModuleAccessorGeneratorVisitorTest extends AstSpecification {
	def "generate"() {
		def definition = """module com.example.test

extern interface JSON

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
JSON[] doSomething()
@nullable int doStatic(@nullable int a, int b)
<T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(ModuleDefinitionSource.fromString("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleAccessorGeneratorVisitor(module)

		expect:
		visitor.visit(module) == """@:final class TestModule {

	static var __module:Dynamic = untyped __js__('Spaghetti["dependencies"]["com.example.test"]["module"]');

	/**
	 * Initializes module.
	 */
	@:deprecated("use doSomething() instead")
	#if !spaghetti_noinline @:extern inline #end
	public static function initModule(a:Int, ?b:Int):Void {
		TestModule.__module.initModule(a, b);
	}
	#if !spaghetti_noinline @:extern inline #end
	public static function doSomething():Array<haxe.Json> {
		return TestModule.__module.doSomething();
	}
	#if !spaghetti_noinline @:extern inline #end
	public static function doStatic(a:Null<Int>, b:Int):Null<Int> {
		return TestModule.__module.doStatic(a, b);
	}
	#if !spaghetti_noinline @:extern inline #end
	public static function returnT<T>(t:T):com.example.test.MyInterface<T> {
		return TestModule.__module.returnT(t);
	}

}
"""
	}
}
