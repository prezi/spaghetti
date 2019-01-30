package com.prezi.spaghetti.typescript.stub

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptInterfaceStubGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {

	interface Tibor<T> {
		getSomeT(): T;
	}

	interface MyInterface<X> extends com.example.test.Tibor<X> {
		/**
		 * Does something.
		 */
		doSomething(): void;

		boolValue(): bool;
		intValue(): int;
		floatValue(): float;
		stringValue(): string;
		anyValue(): any;
		@nullable doSomethingElse(@nullable a: int, b?: int): string[];
		hello<T, U>(f: (X, () -> int) -> U): T[];
	}
}
"""
		def result = parseAndVisitModule(definition, new TypeScriptInterfaceStubGeneratorVisitor(getNamespace()))

		expect:
		result == """export class TiborStub<T> implements Tibor<T> {
	getSomeT():T {
		return null;
	}

}
export class MyInterfaceStub<X> implements MyInterface<X> {
	/**
	 * Does something.
	 */
	doSomething():void {}
	boolValue():boolean {
		return false;
	}
	intValue():number {
		return 0;
	}
	floatValue():number {
		return 0;
	}
	stringValue():string {
		return null;
	}
	anyValue():any {
		return null;
	}
	doSomethingElse(a:number, b?:number):Array<string> {
		return null;
	}
	hello<T, U>(f:(arg0: X, arg1: () => number) => U):Array<T> {
		return null;
	}
	getSomeT():X {
		return null;
	}

}
"""
	}
}
