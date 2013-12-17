package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleProxyGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	private final int moduleIndex

	HaxeModuleProxyGeneratorVisitor(ModuleDefinition module, int moduleIndex)
	{
		super(module)
		this.moduleIndex = moduleIndex
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return ModuleUtils.formatDocumentation(ctx.documentation) +
"""@:final class ${module.name.localName} {
${super.visitModuleDefinition(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		// Do not generate code for types
		return ""
	}

	@Override
	String visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		// Do not generate code for enums
		return ""
	}

	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		// Do not generate code for structs
		return ""
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull @NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)
		returnType = wrapNullable(ctx.annotations(), returnType)

		def params
		def callParams
		if (ctx.parameters) {
			params = ctx.parameters.accept(this)
			callParams = ctx.parameters.elements.collect { it.name.text }.join(", ")
		} else {
			params = ""
			callParams = ""
		}

		return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
"""	@:extern public static inline function ${ctx.name.text}(${params}):${returnType} {
		${returnType == "void"?"":"return "}untyped __modules[${moduleIndex}].${ctx.name.text}(${callParams});
	}
"""
	}
}
