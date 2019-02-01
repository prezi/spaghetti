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
		def result = parseAndVisitModule(definition, new TypeScriptModuleProxyGeneratorVisitor(getNamespace()))

		expect:
		result == """import { TestModule } from "TestModule";
export class __TestModuleProxy {
	doSomething():void {
		TestModule.doSomething();
	}
	doSomethingElse(a:number, b:number):Array<string> {
		return TestModule.doSomethingElse(a, b);
	}
	doSomethingStatic(x:number):number {
		return TestModule.doSomethingStatic(x);
	}
	doSomethingVoid(x:number):void {
		TestModule.doSomethingVoid(x);
	}
	hello<T, U>(t:T, y:U):Array<T> {
		return TestModule.hello<T, U>(t, y);
	}
	returnT<T>(t:T):MyInterface<T> {
		return TestModule.returnT<T>(t);
	}
	public MyConst = MyConst;
	public MyEnum = MyEnum;
}
"""
	}
}
