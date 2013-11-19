package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.SpaghettiModuleBaseVisitor
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import com.prezi.spaghetti.grammar.SpaghettiModuleParser.ModuleDefinitionContext

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
		def typeCollector = new TypeCollectorVisitor(typeNames)
		typeCollector.visit(context)
		def moduleDef = typeCollector.moduleDefinition
		if (moduleDef == null)
		{
			throw new AssertionError("No module defined")
		}
		return moduleDef
	}
}

class TypeCollectorVisitor extends SpaghettiModuleBaseVisitor<Void> {
	final Set<FQName> typeNames
	ModuleDefinition moduleDefinition

	TypeCollectorVisitor(Set<FQName> typeNames) {
		this.typeNames = typeNames
	}

	@Override
	public Void visitModuleDefinition(ModuleDefinitionContext ctx)
	{
		this.moduleDefinition = new ModuleDefinition(FQName.fromContext(ctx.name), ctx)
		super.visitModuleDefinition(ctx)
		return null
	}

	@Override
	public Void visitTypeDefinition(SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def typeName = moduleDefinition.name.resolveLocalName(FQName.fromString(ctx.name.text))
		if (typeNames.contains(typeName)) {
			throw new IllegalStateException("Type already defined: ${typeName}")
		}
		typeNames.add(typeName)
		super.visitTypeDefinition(ctx)
		return null
	}
}
