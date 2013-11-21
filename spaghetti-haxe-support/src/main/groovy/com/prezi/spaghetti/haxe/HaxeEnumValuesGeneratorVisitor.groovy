package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.SpaghettiModuleBaseVisitor
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeEnumValuesGeneratorVisitor extends SpaghettiModuleBaseVisitor<String> {

	@Override
	String visitEnumDefinition(@NotNull @NotNull SpaghettiModuleParser.EnumDefinitionContext ctx)
	{
		def result = ModuleUtils.formatDocumentation(ctx.documentation) \
			+ "@:final class ${ctx.name.text}s {\n"
		ctx.values.eachWithIndex { valueCtx, index ->
			result += ModuleUtils.formatDocumentation(valueCtx.documentation, "\t")
			result += "\tpublic static inline var ${valueCtx.name.text} = ${index};\n"
		}
		result += "}\n"
	}
}
