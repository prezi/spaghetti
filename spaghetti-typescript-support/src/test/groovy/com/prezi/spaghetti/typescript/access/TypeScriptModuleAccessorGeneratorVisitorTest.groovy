package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptModuleAccessorGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate module accessor"() {
		def definition = """
module com.example.test {

	import com.example.other.Other;

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
	returnOther(): Other;
}
"""


		def result = parseAndVisitModule(definition,
				new TypeScriptModuleAccessorGeneratorVisitor(getNamespace()),
				mockInterface("Other", "com.example.other.Other"))

		expect:
		result == """/**
 * Initializes module.
 */
export function initModule(a:number, b?:number):void;
export function doSomething():string;
export function doStatic(a:number, b:number):number;
export function returnT<T>(t:T):MyInterface<T>;
export function returnOther():com_example_other.Other;
"""
	}
}
