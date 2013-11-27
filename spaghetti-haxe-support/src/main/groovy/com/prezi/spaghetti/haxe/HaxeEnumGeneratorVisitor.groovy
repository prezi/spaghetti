package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeEnumGeneratorVisitor extends ModuleBaseVisitor<String> {

	@Override
	String visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		def enumName = ctx.name.text
		def result = ModuleUtils.formatDocumentation(ctx.documentation) +
"""abstract ${enumName}(Int) {
"""

		ctx.values.eachWithIndex { valueCtx, index ->
			result += ModuleUtils.formatDocumentation(valueCtx.documentation, "\t")
			result +=
"""	public static var ${valueCtx.name.text} = new ${enumName}(${index});
"""
		}

		result +=
"""
	static var _values = [ ${ctx.values.collect { it.name.text }.join(", ")} ];
	static var _names =  [ ${ctx.values.collect { "\"${it.name.text}\"" }.join(", ")} ];
	static var _namesToValues = { ${ctx.values.collect { "\"${it.name.text}\": ${it.name.text}" }.join(", ")} };

	inline function new(value:Int) {
		this = value;
	}

	@:to public inline function value():Int {
		return this;
	}

	@:from public static inline function fromValue(value:Int):${enumName} {
		var result = _values[value];
		if (result == null) {
			throw untyped Error("Invlaid value for ${enumName}: " + value);
		}
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
		return result
	}
}
