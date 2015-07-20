package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class HaxeEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def "generate"() {
		def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 1
	@deprecated("escape \\"this\\"!")
	BELA = 2
	GEZA = 4
}
"""
		def result = parseAndVisitEnum(definition, new HaxeEnumGeneratorVisitor())

		expect:
		result == """abstract MyEnum(Int) {
	/**
	 * Alma.
	 */
	public static var ALMA = new MyEnum(1);
	@:deprecated("escape \\"this\\"!")
	public static var BELA = new MyEnum(2);
	public static var GEZA = new MyEnum(4);

	static var _values = ["1" => ALMA, "2" => BELA, "4" => GEZA];
	static var _names = ["1" => "ALMA", "2" => "BELA", "4" => "GEZA"];

	inline function new(value:Int) {
		this = value;
	}

	@:to public function value():Int {
		return this;
	}

	@:from public static function fromValue(value:Int) {
		var key: String = Std.string(value);
		if (!_values.exists(key)) {
			throw "Invalid value for MyEnum: " + value;
		}
		return _values[key];
	}

	@:to public inline function name():String {
		return _names[Std.string(this)];
	}

	@:from public static inline function valueOf(name:String) {
		return switch(name)
		{
			case "ALMA": ALMA;
			case "BELA": BELA;
			case "GEZA": GEZA;
			default: throw "Invalid name for MyEnum: " + name;
		};
	}

	public static function values():Array<MyEnum> {
		return [ALMA, BELA, GEZA];
	}
}
"""
	}
}
