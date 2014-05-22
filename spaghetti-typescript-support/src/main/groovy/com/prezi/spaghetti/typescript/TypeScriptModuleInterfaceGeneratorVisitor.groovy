package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleInterfaceGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	private final String interfaceName

	protected TypeScriptModuleInterfaceGeneratorVisitor(ModuleDefinition module, String interfaceName) {
		super(module)
		this.interfaceName = interfaceName
	}

	@WithJavaDoc
	@Override
	String visitModuleDefinition(@NotNull ModuleParser.ModuleDefinitionContext ctx) {
"""export interface ${interfaceName} {
${visitChildren(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull ModuleParser.TypeDefinitionContext ctx) {
		return ""
	}
}
