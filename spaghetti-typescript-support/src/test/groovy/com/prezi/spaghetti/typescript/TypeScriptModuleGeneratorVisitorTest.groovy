package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleGeneratorVisitorTest extends Specification {
	private static final def DEFINITION = """/**
 * Le module.
 */
module com.example.test

/**
 * My interface.
 */
interface MyInterface<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	// Deprecation is ignored in TypeScript
	@deprecated
	string[] doSomethingElse(int a, int b)
	<T, U> T[] hello(X x, U y)
}

/**
 * My main struct.
 */
struct MyStruct {
	bool what
	/**
	 * Alma.
	 */
	@deprecated
	int alma
	string bela
}

/**
 * Le enum.
 */
enum MyEnum {
	ALPHA
	/**
	 * Beta.
	 */
	BETA
}

/**
 * This is a method that belongs to the module.
 */
int moduleMethod()
"""


	def "generate types"() {
		def module = new DefinitionParserHelper().parse(DEFINITION)
		def visitor = new TypeScriptModuleGeneratorVisitor(module, "ITest", [], false)

		expect:
		visitor.processModule() == EXPECTED_RESULT
	}

	def "generate types and module"() {
		def module = new DefinitionParserHelper().parse(DEFINITION)
		def visitor = new TypeScriptModuleGeneratorVisitor(module, "ITest", [], true)

		expect:
		visitor.processModule() == """
/**
 * Le module.
 */
export interface ITest {

	/**
	 * This is a method that belongs to the module.
	 */
	moduleMethod():number;
}

""" + EXPECTED_RESULT
	}

	private static final def EXPECTED_RESULT = """
/**
 * My interface.
 */
export interface MyInterface<X> {

	/**
	 * Does something.
	 */
	doSomething():void;
	doSomethingElse(a:number, b:number):Array<string>;
	hello<T, U>(x:X, y:U):Array<T>;
}


/**
 * My main struct.
 */
export interface MyStruct {

	/**
	 * My main struct.
	 */
	what: boolean;

	/**
	 * My main struct.
	 */
	alma: number;

	/**
	 * My main struct.
	 */
	bela: string;

}


/**
 * Le enum.
 */
export enum MyEnum {
	ALPHA = 0,

	/**
	 * Beta.
	 */
	BETA = 1
}
"""
}
