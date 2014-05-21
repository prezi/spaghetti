package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeEnumGeneratorVisitor extends ModuleBaseVisitor<String> {

	private class HaxeEnumValueGeneratorVisitor extends ModuleBaseVisitor<String> {
		private final String enumName
		private final int valueIndex

		HaxeEnumValueGeneratorVisitor(String enumName, int valueIndex) {
			this.enumName = enumName
			this.valueIndex = valueIndex
		}

		@WithDeprecation
		@WithJavaDoc
		@Override
		String visitEnumValue(@NotNull ModuleParser.EnumValueContext ctx) {
"""	public static var ${ctx.name.text} = new ${enumName}(${valueIndex});
"""
		}
	}

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		def enumName = ctx.name.text

		def values = []
		ctx.values.eachWithIndex { valueCtx, index ->
			values.add valueCtx.accept(new HaxeEnumValueGeneratorVisitor(enumName, index))
		}

		return \
"""abstract ${enumName}(Int) {
${values.join("")}
	static var _values:Array<${enumName}> = [ ${ctx.values.collect { it.name.text }.join(", ")} ];
	static var _names:Array<String> =  [ ${ctx.values.collect { "\"${it.name.text}\"" }.join(", ")} ];
	static var _namesToValues = { ${ctx.values.collect { "\"${it.name.text}\": ${it.name.text}" }.join(", ")} };

	inline function new(value:Int) {
		this = value;
	}

	@:to public function value():Int {
		return this;
	}

	@:from public static function fromValue(value:Int) {
		if (value < 0 || value >= _values.length) {
			throw untyped Error("Invalid value for ${enumName}: " + value);
		}
		var result = _values[value];
		return result;
	}

	@:to public inline function name():String {
		return _names[this];
	}

	@:from public static inline function valueOf(name:String) {
		var value = untyped _namesToValues[name];
		if (value == null) {
			throw untyped Error("Invalid name for ${enumName}: " + name);
		}
		return value;
	}

	public static function values():Array<${enumName}> {
		return _values.copy();
	}
}
"""
	}
}
