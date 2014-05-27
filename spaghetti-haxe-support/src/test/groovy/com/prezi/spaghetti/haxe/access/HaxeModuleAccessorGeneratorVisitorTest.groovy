package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 24/05/14.
 */
class HaxeModuleAccessorGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

interface MyInterface<T> {}

/**
 * Initializes module.
 */
@deprecated("use doSomething() instead")
void initModule(int a, int b)
string doSomething()
static int doStatic(int a, int b)
static <T> MyInterface<T> returnT(T t)
""")
		def visitor = new HaxeModuleAccessorGeneratorVisitor(module)

		expect:
		visitor.processModule() == """@:final class Test {

	static var __instance:Dynamic = untyped SpaghettiConfiguration["__modules"]["com.example.test"]["__instance"];
	static var __static:Dynamic = untyped SpaghettiConfiguration["__modules"]["com.example.test"]["__static"];

	/**
	 * Initializes module.
	 */
	@:deprecated("use doSomething() instead")
	@:extern public inline function initModule(a:Int, b:Int):Void {
		__instance.initModule(a, b);
	}
	@:extern public inline function doSomething():String {
		return __instance.doSomething();
	}
	@:extern public static inline function doStatic(a:Int, b:Int):Int {
		return __static.doStatic(a, b);
	}
	@:extern public static inline function returnT<T>(t:T):com.example.test.MyInterface<T> {
		return __static.returnT(t);
	}

}
"""
	}
}
