package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.ReservedWords.CONSTANTS
import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.haxe.HaxeGenerator.HAXE_MODULE_VAR

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInitializerGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	HaxeModuleInitializerGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def initializerName = "__" + module.alias + "Init"

		def consts = ctx.moduleElement()*.accept(this) - ""

		def initializerContents =
"""@:keep class ${initializerName} {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		var module:${module.name}.${module.alias} = new ${module.name}.${module.alias}Impl();
		var consts = {
			${consts.join(",\n\t\t\t")}
		};
		untyped ${HAXE_MODULE_VAR} = {
			${MODULE}: module,
			${CONSTANTS}: consts
		}
		return true;
	}
#end
}
"""
		return initializerContents
	}

	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		return "\"${ctx.name.text}\": new __${ctx.name.text}()"
	}

	@Override
	String visitModuleElement(@NotNull @NotNull ModuleParser.ModuleElementContext ctx)
	{
		return ctx.typeDefinition()?.accept(this) ?: ""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		return ctx.constDefinition()?.accept(this) ?: ""
	}
}
