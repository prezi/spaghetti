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
class HaxeEnumTypedefGeneratorVisitor extends SpaghettiModuleBaseVisitor<String> {

	@Override
	String visitEnumDefinition(@NotNull @NotNull SpaghettiModuleParser.EnumDefinitionContext ctx)
	{
		return ModuleUtils.formatDocumentation(ctx.documentation) \
			+ "typedef ${ctx.name.text} = Int;\n"
	}
}
