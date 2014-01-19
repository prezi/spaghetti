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

	private final interfaceTypeParams = []

	HaxeInterfaceGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	private static String defineType(String typeName, List<String> superTypes) {
		def declaration = "interface ${typeName}"
		superTypes.each { superType ->
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
				interfaceTypeParams.add(FQName.fromString(param.name.text))
			}
		}

		def superTypes = ctx.superInterfaceDefinition().collect { superTypeCtx ->
			return superTypeCtx.accept(this)
		}

		def result = ModuleUtils.formatDocumentation(ctx.documentation) +
"""${defineType(typeName, superTypes)}
${ctx.typeElement().collect { elem -> elem.accept(this) }.join("")}
}
"""
		interfaceTypeParams.clear()
		return result
	}

	@Override
	String visitSuperInterfaceDefinition(@NotNull @NotNull ModuleParser.SuperInterfaceDefinitionContext ctx)
	{
		def superType = resolveName(FQName.fromContext(ctx.qualifiedName())).fullyQualifiedName
		superType += ctx.typeArguments()?.accept(this) ?: ""
		return superType
	}

	@Override
	protected FQName resolveName(FQName localTypeName)
	{
		if (interfaceTypeParams.contains(localTypeName))
		{
			return localTypeName
		}
		return super.resolveName(localTypeName)
	}
}
