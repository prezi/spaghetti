package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser

class TypeScriptEnumGeneratorVisitorTest extends AstSpecification {
	def "generate"() {
		def definition = """module com.example.test

enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	BELA
}
"""
		def locator = mockLocator(definition)
		def module = ModuleParser.create(locator.source).parse(mockResolver())
		def visitor = new TypeScriptEnumGeneratorVisitor()

		expect:
		visitor.visit(module) == """export class MyEnum {
	/**
	 * Alma.
	 */
	static ALMA:MyEnum = 0;
	static BELA:MyEnum = 1;

	private static _values:Array<MyEnum> = [ MyEnum.ALMA, MyEnum.BELA ];
	private static _names:Array<string> =  [ "ALMA", "BELA" ];

	static names():Array<string> {
		return MyEnum._names.slice(0);
	}

	static values():Array<MyEnum> {
		return MyEnum._values.slice(0);
	}

	static getName(value:MyEnum) {
		return MyEnum._names[<number> value];
	}

	static getValue(value:MyEnum):number {
		return <number> value;
	}

	static fromValue(value:number):MyEnum {
		if (value < 0 || value >= MyEnum._values.length) {
			throw "Invalid value for MyEnum: " + value;
		}
		return MyEnum._values[value];
	}

	static valueOf(name:String):MyEnum {
		var result:MyEnum;
		switch(name)
		{
			case "ALMA": result = MyEnum.ALMA; break;
			case "BELA": result = MyEnum.BELA; break;
			default: throw Error("Invalid name for MyEnum: " + name);
		};
		return result;
	}
}
"""
	}
}
