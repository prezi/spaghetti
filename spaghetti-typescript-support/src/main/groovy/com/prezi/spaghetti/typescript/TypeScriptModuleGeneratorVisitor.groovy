package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.FQName
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.ModuleUtils
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.Generator.CONFIG
import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.MODULES

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	private final String className
	private final typeParams = []
	private final Collection<ModuleDefinition> dependentModules
	private final boolean localModule

	TypeScriptModuleGeneratorVisitor(ModuleDefinition module, String className, Collection<ModuleDefinition> dependentModules, boolean localModule)
	{
		super(module)
		this.className = className
		this.dependentModules = dependentModules
		this.localModule = localModule
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def methods = []
		def types = []
		def result = ""

		ctx.moduleElement().each {
			if (it.methodDefinition()) {
				methods += it.methodDefinition().accept(this)
			} else {
				types += it.accept(this)
			}
		}

		if (!dependentModules.empty)
		{
			def modulesContents = 
"""
declare var ${CONFIG}:any;
"""
			dependentModules.each { module ->
				modulesContents +=
"""export var ${module.alias}:${module.name}.${module.alias} = ${CONFIG}[\"${MODULES}\"][\"${module.name}\"].${MODULE};
export var __${module.alias}:any = ${CONFIG}[\"${MODULES}\"][\"${module.name}\"].${MODULE};
"""
			}
			modulesContents += "\n"
			result += modulesContents + "\n"
		}

		result +=  ModuleUtils.formatDocumentation(ctx.documentation)
		result += "export interface ${className} {\n" + methods.join("\n") + "\n}\n" + types.join("\n")

		return result + "\n"
	}

	private static String defineType(String typeName, List<String> superTypes) {
		def declaration = "export interface ${typeName}"
		if (!superTypes.empty) {
			declaration += " extends ${superTypes.join(", ")}"
		}
		declaration += " {"
		return declaration
	}

	@WithJavaDoc
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

		def result = \
"""${defineType(typeName, superTypes)}
${ctx.methodDefinition().collect { elem -> elem.accept(this) }.join("")}
}
"""
		typeParams.clear()
		return result
	}

	@WithJavaDoc
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
		def result = "export enum ${enumName} {" + values + "\n}\n"
		return result
	}

	@WithJavaDoc
	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		def values = ctx.propertyDefinition().collect { propertyCtx ->
			return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
				"\t${propertyCtx.property.name.text}: ${propertyCtx.property.type.accept(this)};\n"
		}.join("")

		def structName = ctx.name.text
		def result = "export interface ${structName} {\n" + values + "\n}\n"
		return result
	}

	@WithJavaDoc
	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		def values = visitChildren(ctx)
		def constName = ctx.name.text
		def result = "export class ${constName} {\n" + values + "\n}\n"
		return result
	}

	@Override
	@WithJavaDoc
	String visitConstEntry(@NotNull ModuleParser.ConstEntryContext ctx) {
		return super.visitConstEntry(ctx)
	}

	@Override
	String visitConstEntryDecl(@NotNull ModuleParser.ConstEntryDeclContext ctx) {
		String type
		Token value
		if (ctx.boolValue) {
			type = "boolean"
			value = ctx.boolValue
		} else if (ctx.intValue) {
			type = "number"
			value = ctx.intValue
		} else if (ctx.floatValue) {
			type = "number"
			value = ctx.floatValue
		} else if (ctx.stringValue) {
			type = "string"
			value = ctx.stringValue
		} else {
			throw new IllegalArgumentException("Unknown constant type: " + ctx.dump())
		}
		return "\tstatic ${ctx.name.text}: ${type} = ${value.text};\n"
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
