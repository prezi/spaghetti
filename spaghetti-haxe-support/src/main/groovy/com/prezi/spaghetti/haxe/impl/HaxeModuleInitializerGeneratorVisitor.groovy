package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC
import static com.prezi.spaghetti.haxe.HaxeGenerator.HAXE_MODULE_VAR

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInitializerGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private final Collection<ModuleDefinition> dependencies

	HaxeModuleInitializerGeneratorVisitor(ModuleDefinition module, Collection<ModuleDefinition> dependencies)
	{
		super(module)
		this.dependencies = dependencies
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def initializerName = "__" + module.alias + "Init"

		def instances = []
		dependencies.eachWithIndex { ModuleDefinition dependency, int index ->
			instances.add "var dependency${index}:${dependency.name}.${dependency.alias} = untyped ${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${INSTANCE}\"];"
		}
		def references = (0..<instances.size()).collect { "dependency${it}" }

		def initializerContents =
"""@:keep class ${initializerName} {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		${instances.join("\n\t\t")}
		var module:${module.name}.I${module.alias} = new ${module.name}.${module.alias}(${references.join(", ")});
		var statics = new ${module.name}.__${module.alias}Static();
		untyped ${HAXE_MODULE_VAR} = {
			${INSTANCE}: module,
			${STATIC}: statics
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
