package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	private final typeParams = []
	private final List<ModuleDefinition> dependentModules
	private final boolean localModule

	TypeScriptModuleGeneratorVisitor(ModuleDefinition module, List<ModuleDefinition> dependentModules, boolean localModule)
	{
		super(module)
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
declare var __modules:Array<any>;
"""
			dependentModules.each { module ->
				modulesContents +=
"""export var ${module.name.localName}:${module.name} = __modules[\"${module.name.fullyQualifiedName}\"];
export var __${module.name.localName}:any = __modules[\"${module.name.fullyQualifiedName}\"];
"""
			}
			modulesContents += "\n"
			result += modulesContents + "\n"
		}

		result +=  ModuleUtils.formatDocumentation(ctx.documentation)
		result += "export interface ${module.name.localName} {\n" + methods.join("\n") + "\n}\n" + types.join("\n")

		if (localModule) {
			Set<String> consts = []
			def constContents = ""
			(new ConstCollectorVisitor(module, consts)).processModule();
			
			def ctor = consts.collect { name -> 
				return "\t\tthis.${name} = ${module.name.namespace}.${name};"
			}.join("\n")

			def members = consts.collect { name -> 
				return "\t${name}:__${name};"
			}.join("\n")

			constContents += 
"""
export class __${module.name.localName}Constants {
	constructor() {
${ctor}
	}
${members}
}
"""
			result += constContents
		}

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
		if (!localModule) {
			def values = ctx.propertyDefinition().collect { propertyCtx ->
				return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
					"\tstatic ${propertyCtx.property.name.text}: ${propertyCtx.property.type.accept(this)} = __${module.name.localName}.__consts.${ctx.name.text}.${propertyCtx.property.name.text};"
			}.join("\n")

			def constName = ctx.name.text
			def result = "export class ${constName} {\n" + values + "\n}\n"
			return result
		}
		else {
			def values = ctx.propertyDefinition().collect { propertyCtx -> 
				return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
					"\t${propertyCtx.property.name.text}: ${propertyCtx.property.type.accept(this)};"
			}.join("\n")
			
			def constName = ctx.name.text
			def result = "export interface __${constName} {\n" + values + "\n}\n"
			return result
		}
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


private class ConstCollectorVisitor extends AbstractModuleVisitor<Void> {
	private final Set<String> names

	ConstCollectorVisitor(ModuleDefinition module, Set<String> names) {
		super(module)
		this.names = names
	}

	@Override
	Void visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		names.add(ctx.name.text);
		return null
	}
}








