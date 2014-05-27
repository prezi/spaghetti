package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor
import com.prezi.spaghetti.haxe.WithDeprecation
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleAccessorGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	HaxeModuleAccessorGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return \
"""@:final class ${module.alias} {

	static var ${INSTANCE}:Dynamic = untyped ${CONFIG}[\"${MODULES}\"][\"${module.name}\"][\"${INSTANCE}\"];
	static var ${STATIC}:Dynamic = untyped ${CONFIG}[\"${MODULES}\"][\"${module.name}\"][\"${STATIC}\"];
${visitChildren(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		// Do not generate code for types
		return ""
	}

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitModuleMethodDefinition(@NotNull ModuleParser.ModuleMethodDefinitionContext ctx) {
		return super.visitModuleMethodDefinition(ctx)
	}

	@Override
	protected String visitMethodDefinitionInternal(@NotNull @NotNull @NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)

		def typeParams = ctx.typeParameters()?.accept(this) ?: ""
		def params
		def callParams
		if (ctx.parameters) {
			params = ctx.parameters.accept(this)
			callParams = ctx.parameters.elements.collect { it.name.text }.join(", ")
		} else {
			params = ""
			callParams = ""
		}

		def isStatic = ((ModuleParser.ModuleMethodDefinitionContext) ctx.parent).isStatic
		def delegate = isStatic ? STATIC : INSTANCE

		return \
"""	@:extern public ${isStatic ? "static " : ""}inline function ${ctx.name.text}${typeParams}(${params}):${returnType} {
		${returnType == "Void"?"":"return "}${delegate}.${ctx.name.text}(${callParams});
	}
"""
	}
}
