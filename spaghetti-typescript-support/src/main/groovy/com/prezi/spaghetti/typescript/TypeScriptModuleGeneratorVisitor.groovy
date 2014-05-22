package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.FQName
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.ModuleUtils
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.Generator.CONFIG

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	private final ModuleConfiguration config
	private final String moduleClassName
	private final boolean generateModuleInterface

	TypeScriptModuleGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, String moduleClassName, boolean generateModuleInterface)
	{
		super(module)
		this.config = config
		this.moduleClassName = moduleClassName
		this.generateModuleInterface = generateModuleInterface
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		def result = ""
		def directStaticModules = config.directStaticDependentModules
		if (!directStaticModules.empty)
		{
			result += \
"""declare var ${CONFIG}:any;
"""
			directStaticModules.each { staticModule ->
				result += new TypeScriptModuleProxyGeneratorVisitor(staticModule).processModule()
			}
			result += "\n"
		}

		if (generateModuleInterface) {
			result += new TypeScriptModuleInterfaceGeneratorVisitor(module, moduleClassName).processModule()
		}
		result += new TypeScriptDefinitionIteratorVisitor(module).processModule()

		return result
	}
}
