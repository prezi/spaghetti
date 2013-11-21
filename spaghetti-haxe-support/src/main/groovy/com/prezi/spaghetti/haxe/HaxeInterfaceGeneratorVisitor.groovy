package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeInterfaceGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private final Closure<String> defineType

	HaxeInterfaceGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, Closure<String> defineType)
	{
		super(config, module)
		this.defineType = defineType
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def typeName = ctx.name.text
		FQName superType = null
		if (ctx.superType != null) {
			superType = module.name.resolveLocalName(FQName.fromContext(ctx.superType))
		}

		return ModuleUtils.formatDocumentation(ctx.documentation) +
"""${defineType(typeName, superType)}
${super.visitTypeDefinition(ctx)}
}
"""
	}
}
