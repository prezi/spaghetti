package spaghetti.test;

import spaghetti.test.dependency.DependentEnum;

class TestModule {
	public static function add(a:Int, b:Int):Int {
		return a + b;
	}

	public static function getNext(value:DependentEnum):DependentEnum {
		return DependentEnum.fromValue(value.value() + 1);
	}
}
