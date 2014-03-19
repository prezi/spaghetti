package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeConstGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private String constName

	protected HaxeConstGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	@WithJavaDoc
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		this.constName = ctx.name.text

		def constants = ctx.propertyDefinition().collect { propertyCtx ->
			def propertyName = propertyCtx.property.name.text
			def resolvedPropertyType = propertyCtx.property.type.accept(this)
			return "\tpublic var ${propertyName} (default, null):${resolvedPropertyType};"
		}

		def initializers = ctx.propertyDefinition().collect { propertyCtx ->
			def propertyName = propertyCtx.property.name.text
			return "\t\tthis.${propertyName} = ${constName}.${propertyName};"
		}

		def result = ""


		def deprecatedAnn = ModuleUtils.extractAnnotations(ctx.annotations())["deprecated"]
		if (deprecatedAnn != null) {
			result += Deprecation.annotation(Type.Constant, this.constName, deprecatedAnn) + "\n"
		}

		result += \
"""@:final class __${constName} {
	public function new() {
${initializers.join("\n")}
	}

${constants.join("\n")}
}
"""
		this.constName = null
		return result
	}
}
