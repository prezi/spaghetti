package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.SpaghettiModuleBaseVisitor
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import com.prezi.spaghetti.grammar.SpaghettiModuleParser.ModuleDefinitionContext
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfigurationParser {
	public static ModuleConfiguration parse(Iterable<ModuleDefinitionContext> contexts, Iterable<ModuleDefinitionContext> localContexts) {
		def typeNames = new HashSet<FQName>(ModuleConfiguration.BUILT_IN_TYPE_NAMES)
		def modules = contexts.collect { context ->
			return parseModule(context, typeNames)
		}
		def localModules = localContexts.collect { context ->
			return parseModule(context, typeNames)
		}
		return new ModuleConfiguration(modules + localModules, localModules, typeNames)
	}

	private static ModuleDefinition parseModule(ModuleDefinitionContext context, Set<FQName> typeNames)
	{
		def moduleDef = new ModuleDefinition(FQName.fromContext(context.name), context)
		def typeCollector = new TypeCollectorVisitor(moduleDef, typeNames)
		typeCollector.visit(context)
		return moduleDef
	}
}

class TypeCollectorVisitor extends SpaghettiModuleBaseVisitor<Void> {
	final Set<FQName> typeNames
	final ModuleDefinition moduleDefinition

	TypeCollectorVisitor(ModuleDefinition moduleDefinition, Set<FQName> typeNames) {
		this.moduleDefinition = moduleDefinition
		this.typeNames = typeNames
	}

	@Override
	public Void visitTypeDefinition(@NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		registerTypeName(ctx.name.text)
		return null
	}

	@Override
	Void visitEnumDefinition(@NotNull SpaghettiModuleParser.EnumDefinitionContext ctx)
	{
		registerTypeName(ctx.name.text)
		return null
	}

	private void registerTypeName(String localName)
	{
		def typeName = moduleDefinition.name.resolveLocalName(FQName.fromString(localName))
		if (typeNames.contains(typeName))
		{
			throw new IllegalStateException("Type already defined: ${typeName}")
		}
		typeNames.add(typeName)
	}
}
