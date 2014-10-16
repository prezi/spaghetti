package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.EnumParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser

class KotlinEnumGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	@deprecated("escape \\"this\\"!")
	BELA
	GEZA
}
"""
		def context = ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", definition)).parser.enumDefinition()
		def parser = new EnumParser(context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new KotlinEnumGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """abstract MyEnum(Int) {
	/**
	 * Alma.
	 */
	public static var ALMA = new MyEnum(0);
	@:deprecated("escape \\"this\\"!")
	public static var BELA = new MyEnum(1);
	public static var GEZA = new MyEnum(2);

	static var _values:Array<MyEnum> = [ ALMA, BELA, GEZA ];
	static var _names:Array<String> =  [ "ALMA", "BELA", "GEZA" ];

	inline function new(value:Int) {
		this = value;
	}

	@:to public function value():Int {
		return this;
	}

	@:from public static function fromValue(value:Int) {
		if (value < 0 || value >= _values.length) {
			throw "Invalid value for MyEnum: " + value;
		}
		var result = _values[value];
		return result;
	}

	@:to public inline function name():String {
		return _names[this];
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
		return _values.copy();
	}
}
"""
	}
}
