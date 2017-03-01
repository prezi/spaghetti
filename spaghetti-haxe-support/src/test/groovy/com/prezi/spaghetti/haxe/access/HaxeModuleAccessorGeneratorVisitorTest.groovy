package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class HaxeModuleAccessorGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {

	extern interface JSON;

	interface MyInterface<T> {
		/**
		 * This should not influence anything.
		 */
		add(a: int, b: int): int;
	}

	/**
	 * Initializes module.
	 */
	@deprecated("use doSomething() instead")
	initModule(a: int, b?: int): void;
	doSomething(): JSON[];
	@nullable doStatic(@nullable a: int, b: int): int;
	returnT<T>(t: T): MyInterface<T>;
}
"""

		def result = parseAndVisitModule(definition, new HaxeModuleAccessorGeneratorVisitor(ModuleFormat.Wrapperless))

		expect:
		result == """@:final class TestModule {

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
