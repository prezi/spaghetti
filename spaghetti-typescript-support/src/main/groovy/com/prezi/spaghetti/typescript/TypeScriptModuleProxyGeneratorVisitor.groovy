package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.Generator.CONFIG
import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.MODULES

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleProxyGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	protected TypeScriptModuleProxyGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@Override
	String visitModuleDefinition(@NotNull ModuleParser.ModuleDefinitionContext ctx) {
		return "export var ${module.alias}:${module.name}.${module.alias} = ${CONFIG}[\"${MODULES}\"][\"${module.name}\"].${MODULE};\n"
	}
}
