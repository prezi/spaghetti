package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptModuleProxyGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {

	enum MyEnum {
		Apple
	}

	const MyConst {
		apple: string = "apple";
	}

	interface MyInterface<T> {
		/**
		 * This should have nothing to do with the results.
		 */
		someDummyMethod(x: int): void;
	}
	/**
	 * Does something.
	 */
	doSomething(): void;

	doSomethingElse(a: int, b: int): string[];
	/**
	 * No JavaDoc should be generated, this is a non-user-visible class.
	 */
	@deprecated("This should be ignored, too")
	doSomethingStatic(x: int): int;
	doSomethingVoid(x: int): void;
	hello<T, U>(t: T, y: U): T[];
	returnT<T>(t: T): MyInterface<T>;
}
"""
		def result = parseAndVisitModule(definition, new TypeScriptModuleProxyGeneratorVisitor())

		expect:
		result == """export class __TestModuleProxy {
	doSomething():void {
		com.example.test.TestModule.doSomething();
	}
	doSomethingElse(a:number, b:number):Array<string> {
		return com.example.test.TestModule.doSomethingElse(a, b);
	}
	doSomethingStatic(x:number):number {
		return com.example.test.TestModule.doSomethingStatic(x);
	}
	doSomethingVoid(x:number):void {
		com.example.test.TestModule.doSomethingVoid(x);
	}
	hello<T, U>(t:T, y:U):Array<T> {
		return com.example.test.TestModule.hello(t, y);
	}
	returnT<T>(t:T):com.example.test.MyInterface<T> {
		return com.example.test.TestModule.returnT(t);
	}
	public MyConst = com.example.test.MyConst;
	public MyEnum = com.example.test.MyEnum;
}
"""
	}
}
