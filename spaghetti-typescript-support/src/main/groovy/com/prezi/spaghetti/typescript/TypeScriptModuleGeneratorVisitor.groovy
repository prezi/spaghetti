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

	private final typeParams = []
	private final List<ModuleDefinition> dependentModules

	TypeScriptModuleGeneratorVisitor(ModuleDefinition module, List<ModuleDefinition> dependentModules)
	{
		super(module)
		this.dependentModules = dependentModules
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

		def modulesContents = ""
		if (!dependentModules.empty)
		{
			modulesContents =
"""
declare var __modules:Array<any>;
"""
			dependentModules.each { module ->
				modulesContents +=
"""export var ${module.name.localName}:${module.name} = __modules[\"${module.name.fullyQualifiedName}\"];
export var __${module.name.localName}:any = __modules[\"${module.name.fullyQualifiedName}\"];
"""
			}
			modulesContents += "\n"
		}

		def docs = ModuleUtils.formatDocumentation(ctx.documentation)
		return modulesContents + "\n" + docs + "export interface ${module.name.localName} {\n" + methods.join("") + "}\n" + types.join("\n")
	}

	private static String defineType(String typeName, List<String> superTypes) {
		def declaration = "export interface ${typeName}"
		if (!superTypes.empty) {
			declaration += " extends ${superTypes.join(", ")}"
		}
		declaration += " {"
		return declaration
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

		def superTypes = ctx.superInterfaceDefinition().collect { superTypeCtx ->
			return superTypeCtx.accept(this)
		}

		def result = ModuleUtils.formatDocumentation(ctx.documentation) +
				"""${defineType(typeName, superTypes)}
${ctx.methodDefinition().collect { elem -> elem.accept(this) }.join("")}
}
"""
		typeParams.clear()
		return result
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
		def values = ctx.propertyDefinition().collect { propertyCtx ->
			return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
				"\t${propertyCtx.property.name.text}: ${propertyCtx.property.type.accept(this)};\n"
		}.join("")

		def structName = ctx.name.text
		def docs = ModuleUtils.formatDocumentation(ctx.documentation)
		def result = docs + "export interface ${structName} {\n" + values + "\n}\n"
		return result
	}

	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		def values = ctx.propertyDefinition().collect { propertyCtx ->
			return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
				"\tstatic ${propertyCtx.property.name.text}: ${propertyCtx.property.type.accept(this)} = __${module.name.localName}.__consts.${ctx.name.text}.${propertyCtx.property.name.text};\n"
		}.join("")

		def constName = ctx.name.text
		def docs = ModuleUtils.formatDocumentation(ctx.documentation)
		def result = docs + "export class ${constName} {\n" + values + "\n}\n"
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
		if (typeParams.contains(localTypeName))
		{
			return localTypeName
		}
		return super.resolveName(localTypeName)
	}
}
