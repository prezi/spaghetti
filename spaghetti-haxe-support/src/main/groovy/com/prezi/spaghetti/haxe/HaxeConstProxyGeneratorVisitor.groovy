package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.ReservedWords.CONSTANTS

/**
 * Created by lptr on 16/11/13.
 */
class HaxeConstProxyGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private String constName

	protected HaxeConstProxyGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		this.constName = ctx.name.text

		def constants = ctx.propertyDefinition().collect { propertyCtx ->
			propertyCtx.accept(this)
		}

		def result = \
"""@:final class ${constName} {
${constants.join("\n")}
}
"""
		this.constName = null
		return result
	}

	@WithJavaDoc
	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def propertyName = ctx.property.name.text
		def resolvedPropertyType = ctx.property.type.accept(this)
		return "\tpublic static var ${propertyName} (default, null):${resolvedPropertyType} = untyped __modules[\"${module.name}\"].${CONSTANTS}.${constName}.${propertyName};"
	}
}
