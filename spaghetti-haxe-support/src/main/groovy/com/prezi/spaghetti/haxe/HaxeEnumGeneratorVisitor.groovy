package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.SpaghettiModuleBaseVisitor
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeEnumGeneratorVisitor extends SpaghettiModuleBaseVisitor<String> {

	@Override
	String visitEnumDefinition(@NotNull @NotNull SpaghettiModuleParser.EnumDefinitionContext ctx)
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
	static inline var guard = ${ctx.values.size()};

	inline function new(value:Int) {
		this = value;
	}

	@:to public inline function value():Int
	{
		return this;
	}

	@:from public static inline function fromValue(value:Int):${enumName}
	{
		if (value < 0 || value >= guard) {
			throw untyped Error("Invlaid value for ${enumName}: " + value);
		}
		return new ${enumName}(value);
	}
}
"""
		return result
	}
}
