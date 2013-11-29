package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	TypeScriptModuleGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def methods = []
		def types = []
		ctx.moduleElement().each {
			if (it.methodDefinition()) {
				methods += it.methodDefinition().accept(this)
			} else {
				types += it.accept(this)
			}
		}

		def docs = ModuleUtils.formatDocumentation(ctx.documentation)
		return docs + "export interface ${module.name.localName} {\n" + methods.join("") + "}\n" + types.join("\n")
	}

	private static String defineType(String typeName, FQName superType) {
		def declaration = "export interface ${typeName}"
		if (superType != null)
		{
			declaration += " extends ${superType.fullyQualifiedName}"
		}
		declaration += " {"
		return declaration
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		def typeName = ctx.name.text
		FQName superType = null
		if (ctx.superType != null) {
			superType = module.name.qualifyLocalName(FQName.fromContext(ctx.superType))
		}

		return ModuleUtils.formatDocumentation(ctx.documentation) +
				"""${defineType(typeName, superType)}
${super.visitTypeDefinition(ctx)}
}
"""
	}

	@Override
	String visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		def valueLines = []
		ctx.values.eachWithIndex{ valueCtx, index ->
			def val = ModuleUtils.formatDocumentation(valueCtx.documentation, "\t")
			val += "\t${valueCtx.name.text} = ${index}"
			valueLines += val
		}
		def values = valueLines.join(",\n\t")

		def enumName = ctx.name.text
		def docs = ModuleUtils.formatDocumentation(ctx.documentation)
		def result = docs + "export enum ${enumName} {" + values + "\n}\n"
		return result
	}

	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		def valueLines = []
		def values = ctx.propertyDefinition().collect { propertyCtx ->
			return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
				"\t${propertyCtx.property.name.text}: ${propertyCtx.property.type.accept(this)};\n"
		}.join("")

		def structName = ctx.name.text
		def docs = ModuleUtils.formatDocumentation(ctx.documentation)
		def result = docs + "export interface ${structName} {" + values + "\n}\n"
		return result
	}
}
