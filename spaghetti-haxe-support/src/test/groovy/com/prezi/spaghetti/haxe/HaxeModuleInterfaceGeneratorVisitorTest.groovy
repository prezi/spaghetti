package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class HaxeModuleInterfaceGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

interface MyInterface<T> {}
/**
 * Does something.
 */
void doSomething()

string[] doSomethingElse(int a, int b)
<T, U> T[] hello(T t, U y)
<T> MyInterface<T> returnT(T t)
""")
		def visitor = new HaxeModuleInterfaceGeneratorVisitor(module, "IInterface")

		expect:
		visitor.processModule() == """interface IInterface {

	/**
	 * Does something.
	 */
	function doSomething():Void;
	function doSomethingElse(a:Int, b:Int):Array<String>;
	function hello<T, U>(t:T, y:U):Array<T>;
	function returnT<T>(t:T):com.example.test.MyInterface<T>;

}
"""
	}
}
