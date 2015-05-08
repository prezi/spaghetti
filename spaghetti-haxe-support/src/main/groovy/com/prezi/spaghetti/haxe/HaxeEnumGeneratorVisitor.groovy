package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.generator.EnumGeneratorUtils

class HaxeEnumGeneratorVisitor extends StringModuleVisitorBase {
	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name

		def namesToValues = EnumGeneratorUtils.calculateEnumValues(node)
		def valueVisitor = new EnumValueVisitor(enumName, namesToValues)
		def values = []
		node.values.each { value ->
			values.add value.accept(valueVisitor)
		}

		return \
"""abstract ${enumName}(Int) {
${values.join("\n")}

	static var _values:Array<${enumName}> = [ ${node.values.join(", ")} ];
	static var _names:Array<String> =  [ ${node.values.collect { "\"${it}\"" }.join(", ")} ];

	inline function new(value:Int) {
		this = value;
	}

	@:to public function value():Int {
		return this;
	}

	@:from public static function fromValue(value:Int) {
		if (value < 0 || value >= _values.length) {
			throw "Invalid value for ${enumName}: " + value;
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
${node.values.collect {"			case \"${it}\": ${it};"}.join("\n")}
			default: throw "Invalid name for ${enumName}: " + name;
		};
	}

	public static function values():Array<${enumName}> {
		return _values.copy();
	}
}
"""
	}

	private static class EnumValueVisitor extends AbstractHaxeGeneratorVisitor {
		private final String enumName
		private final Map<String, Integer> namesToValues

		EnumValueVisitor(String enumName, Map<String, Integer> namesToValues) {
			this.enumName = enumName
			this.namesToValues = namesToValues
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\tpublic static var ${node.name} = new ${enumName}(${namesToValues[node.name]});"
		}
	}
}
