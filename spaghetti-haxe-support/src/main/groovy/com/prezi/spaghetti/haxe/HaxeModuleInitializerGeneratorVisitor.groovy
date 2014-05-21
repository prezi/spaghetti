package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.Generator.CONFIG
import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.haxe.HaxeGenerator.HAXE_MODULE_VAR

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInitializerGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private final Collection<ModuleDefinition> dynamicDependencies

	HaxeModuleInitializerGeneratorVisitor(ModuleDefinition module, Collection<ModuleDefinition> dynamicDependencies)
	{
		super(module)
		this.dynamicDependencies = dynamicDependencies
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def initializerName = "__" + module.alias + "Init"

		def dynamicInstances = []
		dynamicDependencies.eachWithIndex { ModuleDefinition dependency, int index ->
			dynamicInstances.add "var dependency${index}:${dependency.name}.${dependency.alias} = untyped ${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${MODULE}\"];"
		}
		def dynamicReferences = ["untyped ${CONFIG}"] + (0..<dynamicInstances.size()).collect { "dependency${it}" }

		def initializerContents =
"""@:keep class ${initializerName} {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		${dynamicInstances.join("\n\t\t")}
		var module:${module.name}.I${module.alias} = new ${module.name}.${module.alias}(${dynamicReferences.join(", ")});
		untyped ${HAXE_MODULE_VAR} = {
			${MODULE}: module
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
