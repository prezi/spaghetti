package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeConstProxyGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private String constName

	protected HaxeConstProxyGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		this.constName = ctx.name.text

		def constants = ctx.propertyDefinition().collect { propertyCtx ->
			propertyCtx.accept(this)
		}

		def result = ModuleUtils.formatDocumentation(ctx.documentation) +
"""@:final class ${constName} {
${constants.join("\n")}
}
"""
		this.constName = null
		return result
	}

	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def propertyName = ctx.property.name.text
		def resolvedPropertyType = ctx.property.type.accept(this)
		return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
		"\tpublic static var ${propertyName} (default, null):${resolvedPropertyType} = untyped ${module.name.localName}.__consts.${constName}.${propertyName};"
	}
}
