package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeTypeTypedefGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	HaxeTypeTypedefGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module)
	{
		super(config, module)
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		return generateTypeDefinition(ctx) { String typeName, FQName superType ->
			def declaration = "typedef ${typeName} = {"
			if (superType != null) {
				declaration += " > ${superType.fullyQualifiedName},"
			}
			return declaration
		}
	}
}
