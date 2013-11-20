package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private final Closure<String> defineType

	HaxeModuleGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, Closure<String> defineType)
	{
		super(config, module)
		this.defineType = defineType
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		return addDocumentationIfNecessary(ctx.documentation) \
			+ defineType(module.name.localName) \
			+ super.visitModuleDefinition(ctx) \
			+ "}"
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		// Do not generate code for types
		return ""
	}
}
