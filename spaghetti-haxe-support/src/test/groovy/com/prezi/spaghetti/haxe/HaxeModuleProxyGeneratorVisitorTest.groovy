package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class HaxeModuleProxyGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

/**
 * Initializes module.
 */
void initModule(int a, int b)
string doSomething()
""")
		def visitor = new HaxeModuleProxyGeneratorVisitor(module)

		expect:
		visitor.processModule() == """@:final class Test {

	/**
	 * Initializes module.
	 */
	@:extern public static inline function initModule(a:Int, b:Int):Void {
		return untyped __config["__modules"]["com.example.test"].__module.initModule(a, b);
	}
	@:extern public static inline function doSomething():String {
		return untyped __config["__modules"]["com.example.test"].__module.doSomething();
	}

}
"""
	}
}
