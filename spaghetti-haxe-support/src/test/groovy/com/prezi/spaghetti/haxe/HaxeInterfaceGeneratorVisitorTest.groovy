package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class HaxeInterfaceGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

interface MyInterface<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	string[] doSomethingElse(int a, int b)
	<T, U> T[] hello(X x, U y);
}
""")
		def visitor = new HaxeInterfaceGeneratorVisitor(module)

		expect:
		visitor.processModule() == """interface MyInterface<X> {

	/**
	 * Does something.
	 */
	function doSomething():Void;
	function doSomethingElse(a:Int, b:Int):Array<String>;
	function hello<T, U>(x:X, y:U):Array<T>;

}
"""
	}
}
