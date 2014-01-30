package com.prezi.spaghetti.json

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 30/01/14.
 */
class JsonVisitor extends AbstractModuleVisitor<Object> {

	private final methodTypeParams = []
	private final interfaceTypeParams = []

	JsonVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	Object visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def types = ctx.moduleElement().collect() { it.typeDefinition() } - null
		def consts = types.collect() { it.constDefinition() } - null
		def enums = types.collect() { it.enumDefinition() } - null
		def interfaces = types.collect() { it.interfaceDefinition() } - null
		def structs = types.collect() { it.structDefinition() } - null
		def methods = ctx.moduleElement().collect() { it.methodDefinition() } - null
		process ctx, [
				consts: consts*.accept(this),
				enums: enums*.accept(this),
				interfaces: interfaces*.accept(this),
				structs: structs*.accept(this),
				methods: methods*.accept(this)
		]
	}

	@Override
	Object visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		process ctx, [
				values: ctx.propertyDefinition()*.accept(this)
		]
	}

	@Override
	Object visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		process ctx, [
				names: ctx.values*.accept(this)
		]
	}

	@Override
	Object visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
	{
		def data = [:]
		def typeParamsCtx = ctx.typeParameters()
		if (typeParamsCtx) {
			def typeParams = []
			typeParamsCtx.parameters.each { param ->
				typeParams += param.name.text
				interfaceTypeParams.add(FQName.fromString(param.name.text))
			}
			data.put("parameters", typeParams)
		}
		data.put("methods", ctx.methodDefinition()*.accept(this))
		def result = process ctx, data
		interfaceTypeParams.clear()
		return result
	}

	@Override
	Object visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		process ctx, [
		        properties: ctx.propertyDefinition()*.accept(this)
		]
	}

	@Override
	Object visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def typeParams = ctx.typeParameters()
		typeParams?.parameters?.each { param ->
			methodTypeParams.add(FQName.fromString(param.name.text))
		}
		def result = process ctx, [
		        parameters: ctx.parameters?.elements*.accept(this) ?: [],
				returnType: ctx.returnTypeChain().accept(this)
		]
		methodTypeParams.clear()
		return result
	}

	@Override
	Object visitEnumValue(@NotNull @NotNull ModuleParser.EnumValueContext ctx)
	{
		process ctx, [:]
	}

	@Override
	Object visitTypeNamePair(@NotNull @NotNull ModuleParser.TypeNamePairContext ctx)
	{
		process ctx, [ type: ctx.type.accept(this) ]
	}

	@Override
	Object visitVoidReturnTypeChain(@NotNull @NotNull ModuleParser.VoidReturnTypeChainContext ctx)
	{
		ctx.voidType().accept(this)
	}

	@Override
	Object visitNormalReturnTypeChain(@NotNull @NotNull ModuleParser.NormalReturnTypeChainContext ctx)
	{
		ctx.typeChain().accept(this)
	}

	@Override
	Object visitTypeChain(@NotNull @NotNull ModuleParser.TypeChainContext ctx)
	{
		ctx.valueType()?.accept(this) ?: ctx.callbackTypeChain()?.accept(this)
	}

	@Override
	Object visitCallbackTypeChain(@NotNull @NotNull ModuleParser.CallbackTypeChainContext ctx)
	{
		[
				chain: ctx.elements*.accept(this)
		]
	}

	@Override
	Object visitSimpleTypeChainElement(@NotNull @NotNull ModuleParser.SimpleTypeChainElementContext ctx)
	{
		ctx.returnType().accept(this)
	}

	@Override
	Object visitSubTypeChainElement(@NotNull @NotNull ModuleParser.SubTypeChainElementContext ctx)
	{
		ctx.typeChain().accept(this)
	}

	@Override
	Object visitValueType(@NotNull @NotNull ModuleParser.ValueTypeContext ctx)
	{
		def type = ctx.getChild(0).accept(this)
		for (it in ctx.ArrayQualifier()) {
			type = [ array: type ]
		}
		return type
	}

	@Override
	Object visitVoidType(@NotNull @NotNull ModuleParser.VoidTypeContext ctx)
	{
		"void"
	}

	@Override
	Object visitPrimitiveType(@NotNull @NotNull ModuleParser.PrimitiveTypeContext ctx)
	{
		ctx.text
	}

	@Override
	Object visitModuleType(@NotNull @NotNull ModuleParser.ModuleTypeContext ctx)
	{
		resolveName(FQName.fromContext(ctx.name))
	}

	@Override
	Object visitAnnotation(@NotNull @NotNull ModuleParser.AnnotationContext ctx)
	{
		process ctx, (ctx.annotationParameters()?.accept(this) as Map<String, Object>) ?: [:]
	}

	@Override
	Object visitSingleAnnotationParameter(
			@NotNull @NotNull ModuleParser.SingleAnnotationParameterContext ctx)
	{
		[
		        parameters: [
						default: ctx.annotationValue().accept(this)
				]
		]
	}

	@Override
	Object visitMultipleAnnotationParameters(
			@NotNull @NotNull ModuleParser.MultipleAnnotationParametersContext ctx)
	{
		[
		        parameters: ctx.annotationParameter().collectEntries { [it.name.text, it.annotationValue().accept(this) ] }
		]
	}

	@Override
	Object visitAnnotationNullParameter(@NotNull @NotNull ModuleParser.AnnotationNullParameterContext ctx)
	{
		null
	}

	@Override
	Object visitAnnotationBooleanParameter(
			@NotNull @NotNull ModuleParser.AnnotationBooleanParameterContext ctx)
	{
		ctx.boolValue.text == "true"
	}

	@Override
	Object visitAnnotationNumberParameter(@NotNull @NotNull ModuleParser.AnnotationNumberParameterContext ctx)
	{
		ctx.numberValue.text.toFloat()
	}

	@Override
	Object visitAnnotationStringParameter(@NotNull @NotNull ModuleParser.AnnotationStringParameterContext ctx)
	{
		def value = ctx.stringValue.text
		value.substring(1, value.length() - 1)
	}

	private String resolveName(FQName localName) {
		if (interfaceTypeParams.contains(localName))
		{
			return localName
		}
		if (methodTypeParams.contains(localName))
		{
			return localName
		}
		return module.resolveName(localName)
	}

	private Map<String, Map<String, Object>> process(def ctx, Map<String, Object> map) {
		def result = [:]
		if (ctx.metaClass.hasProperty(ctx, "name")) {
			def name = ctx.name
			if (name?.text) {
				result.put("name", name.text)
			}
		}

		if (ctx.metaClass.respondsTo(ctx, "annotations")) {
			def annotations = ctx.annotations()?.annotation()
			if (annotations) {
				result.put("annotations", annotations*.accept(this))
			}
		}

		if (ctx.metaClass.hasProperty(ctx, "documentation")) {
			def doc = ctx.documentation
			if (doc?.text) {
				result.put("documentation", "\n" + doc.text)
			}
		}

		result.putAll(map)

		return result
	}
}
