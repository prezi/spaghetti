package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleTypedefGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	HaxeModuleTypedefGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module)
	{
		super(config, module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		return addDocumentationIfNecessary(ctx.documentation) \
			+ """typedef ${module.name.localName} = {
${super.visitModuleDefinition(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		return ""
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		return generateMethodDefinition(ctx)
	}
}
