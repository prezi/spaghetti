package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

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
		def initializerName = "__" + module.name.localName + "Init"

		def consts = ctx.moduleElement()*.accept(this) - ""

		def initializerContents =
"""@:keep class ${initializerName} {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		var module:${module.name.localName} = new ${module.name.localName}Impl();
		var consts = {
			${consts.join(",\n\t\t\t")}
		};
		untyped module.__consts = consts;
		untyped __module = module;
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
