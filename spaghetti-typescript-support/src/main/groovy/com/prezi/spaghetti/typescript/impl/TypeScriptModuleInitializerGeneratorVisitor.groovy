package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC
import static com.prezi.spaghetti.typescript.TypeScriptGenerator.CREATE_MODULE_FUNCTION

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleInitializerGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	private final Collection<ModuleDefinition> dependencies

	TypeScriptModuleInitializerGeneratorVisitor(ModuleDefinition module, Collection<ModuleDefinition> dependencies)
	{
		super(module)
		this.dependencies = dependencies
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx) {
		def instances = []

		dependencies.eachWithIndex { ModuleDefinition dependency, int index ->
			instances.add "var dependency${index}:${dependency.name}.${dependency.alias} = ${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${INSTANCE}\"];"
		}
		def references = (0..<instances.size()).collect { "dependency${it}" }
		return \
"""export function ${CREATE_MODULE_FUNCTION}():any {
	${instances.join("\n\t")}
	var module:${module.name}.I${module.alias} = new ${module.name}.${module.alias}(${references.join(", ")});
	var statics = new ${module.name}.__${module.alias}Static();
	return {
		${INSTANCE}: module,
		${STATIC}: statics
	}
}
"""
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
