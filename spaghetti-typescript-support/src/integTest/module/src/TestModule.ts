module spaghetti.test {

export class TestModule {
	static add(a:number, b:number):number {
		return a + b;
	}

	static getNext(value:spaghetti.test.dependency.DependentEnum):spaghetti.test.dependency.DependentEnum {
		return value + 1;
	}
}

}
