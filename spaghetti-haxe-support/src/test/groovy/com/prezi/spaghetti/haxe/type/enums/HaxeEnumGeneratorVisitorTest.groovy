package com.prezi.spaghetti.haxe.type.enums

import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.EnumGeneratorSpecification
import com.prezi.spaghetti.haxe.type.enums.HaxeEnumGeneratorVisitor

class HaxeEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 1,
	@deprecated("escape \\"this\\"!")
	BELA = 2,
	GEZA = 4
}
"""

	def "generate local definition"() {
		def result = parseAndVisitEnum(definition, new HaxeEnumGeneratorVisitor())

		expect:
		result == expectedWith("1", "2", "4")
	}

	def "generate dependent definition for UMD format"() {
		def result = parseAndVisitEnum(definition, new HaxeDependentEnumGeneratorVisitor("test", ModuleFormat.UMD))

		expect:
		result == expectedWith(
				"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"MyEnum\"][\"ALMA\"]')",
				"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"MyEnum\"][\"BELA\"]')",
				"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"MyEnum\"][\"GEZA\"]')")
	}

	def "generate dependent definition for wrapperless format"() {
		def result = parseAndVisitEnum(definition, new HaxeDependentEnumGeneratorVisitor("test", ModuleFormat.Wrapperless))

		expect:
		result == expectedWith(
				"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyEnum\"][\"ALMA\"]')",
				"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyEnum\"][\"BELA\"]')",
				"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyEnum\"][\"GEZA\"]')")
	}

	private static String expectedWith(String first, String second, String third) {
		return """abstract MyEnum(Int) {
	/**
	 * Alma.
	 */
	public static var ALMA (default, never) = new MyEnum(${first});
	@:deprecated("escape \\"this\\"!")
	public static var BELA (default, never) = new MyEnum(${second});
	public static var GEZA (default, never) = new MyEnum(${third});

	static var _values = {
		#if js
		var thisValue = MyEnum;
		untyped __js__("thisValue[thisValue.ALMA] = 'ALMA'");
		untyped __js__("thisValue[thisValue.BELA] = 'BELA'");
		untyped __js__("thisValue[thisValue.GEZA] = 'GEZA'");
		#end
		[Std.string(ALMA) => ALMA, Std.string(BELA) => BELA, Std.string(GEZA) => GEZA];
	}
	static var _names = [Std.string(ALMA) => "ALMA", Std.string(BELA) => "BELA", Std.string(GEZA) => "GEZA"];

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
