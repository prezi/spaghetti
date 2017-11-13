package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.ModuleGeneratorSpecification
import spock.lang.Ignore

class TypeScriptModuleAccessorGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate UMD"() {
		def definition = """
module com.example.test {

	interface MyInterface<T> {
		/**
		 * This should have nothing to do with the results.
		 */
		someDummyMethod(x: int): void;
	}
	/**
	 * Initializes module.
	 */
	@deprecated("use doSomething() instead")
	initModule(a: int, b?: int): void;
	doSomething(): string;
	doStatic(a: int, b: int): int;
	returnT<T>(t: T): MyInterface<T>;
}
"""

		def result = parseAndVisitModule(definition, new TypeScriptModuleAccessorGeneratorVisitor(ModuleFormat.UMD))

		expect:
		result == """export interface TestModule {
	/**
	 * Initializes module.
	 */
	initModule(a:number, b?:number):void;
	doSomething():string;
	doStatic(a:number, b:number):number;
	returnT<T>(t:T):com.example.test.MyInterface<T>;

}
export var TestModule:TestModule;
com.example.test = Spaghetti["dependencies"]["com.example.test"];
"""
	}

	def "generate wrapperless"() {
		def definition = """
module com.example.test {

	interface MyInterface<T> {
		/**
		 * This should have nothing to do with the results.
		 */
		someDummyMethod(x: int): void;
	}
	/**
	 * Initializes module.
	 */
	@deprecated("use doSomething() instead")
	initModule(a: int, b?: int): void;
	doSomething(): string;
	doStatic(a: int, b: int): int;
	returnT<T>(t: T): MyInterface<T>;
}
"""

		def result = parseAndVisitModule(definition, new TypeScriptModuleAccessorGeneratorVisitor(ModuleFormat.Wrapperless))

		expect:
		result == """export interface TestModule {
	/**
	 * Initializes module.
	 */
	initModule(a:number, b?:number):void;
	doSomething():string;
	doStatic(a:number, b:number):number;
	returnT<T>(t:T):com.example.test.MyInterface<T>;

}
export var TestModule:TestModule;
com.example.test = Spaghetti["dependencies"]["com.example.test"]["module"];
"""
	}
}
