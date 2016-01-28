package com.prezi.spaghetti.haxe.type.enums

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor

class HaxeEnumGeneratorVisitor extends StringModuleVisitorBase {
	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name

		return \
"""abstract ${enumName}(Int) {
${node.values*.accept(createEnumValueVisitor(node.name)).join("\n")}

	static var _values = [${node.values.collect { entry -> "Std.string(${entry.name}) => ${entry.name}" }.join(", ")}];
	static var _names = [${node.values.collect { entry -> "Std.string(${entry.name}) => \"${entry.name}\"" }.join(", ")}];

	inline function new(value:Int) {
		this = value;
	}

	@:to public function value():Int {
		return this;
	}

	@:from public static function fromValue(value:Int) {
		var key: String = Std.string(value);
		if (!_values.exists(key)) {
			throw "Invalid value for ${enumName}: " + value;
		}
		return _values[key];
	}

	@:to public inline function name():String {
		return _names[Std.string(this)];
	}

	@:from public static inline function valueOf(name:String) {
		return switch(name)
		{
${node.values.collect {"			case \"${it}\": ${it};"}.join("\n")}
			default: throw "Invalid name for ${enumName}: " + name;
		};
	}

	public static function values():Array<${enumName}> {
		return [${node.values.collect { it }.join(", ")}];
	}
}
"""
	}

	protected EnumValueVisitor createEnumValueVisitor(String enumName) {
		return new EnumValueVisitor(enumName);
	}

	protected class EnumValueVisitor extends AbstractHaxeGeneratorVisitor {
		private final String enumName

		EnumValueVisitor(String enumName) {
			this.enumName = enumName
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\tpublic static var ${node.name} = new ${enumName}(${generateValueExpression(node)});"
		}

		String generateValueExpression(EnumValueNode node) {
			return node.value.toString()
		}
	}
}
