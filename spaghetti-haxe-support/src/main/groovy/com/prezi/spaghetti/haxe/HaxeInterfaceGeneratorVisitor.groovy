package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	private final typeParams = []

	HaxeInterfaceGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	private static String defineType(String typeName, String superType) {
		def declaration = "interface ${typeName}"
		if (superType != null)
		{
			declaration += " extends ${superType}"
		}
		return declaration + " {"
	}

	@Override
	String visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
	{
		def typeName = ctx.name.text
		def typeParamsCtx = ctx.typeParameters()
		if (typeParamsCtx != null) {
			typeName += typeParamsCtx.accept(this)
			typeParamsCtx.parameters.each { param ->
				typeParams.add(FQName.fromString(param.name.text))
			}
		}

		String superType = null
		if (ctx.superType != null) {
			superType = resolveName(FQName.fromContext(ctx.superType)).fullyQualifiedName
			superType += ctx.typeArguments()?.accept(this) ?: ""
		}

		def result = ModuleUtils.formatDocumentation(ctx.documentation) +
"""${defineType(typeName, superType)}
${ctx.typeElement().collect { elem -> elem.accept(this) }.join("")}
}
"""
		typeParams.clear()
		return result
	}

	@Override
	protected FQName resolveName(FQName localTypeName)
	{
		if (typeParams.contains(localTypeName))
		{
			return localTypeName
		}
		return super.resolveName(localTypeName)
	}
}
