package com.prezi.gradle.spaghetti

import prezi.spaghetti.SpaghettiModuleBaseVisitor
import prezi.spaghetti.SpaghettiModuleParser
import prezi.spaghetti.SpaghettiModuleParser.ModuleDefinitionContext

/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfigurationParser {
	public static ModuleConfiguration parse(ModuleDefinitionContext... contexts) {
		def typeNames = new HashSet<FQName>(ModuleConfiguration.BUILT_IN_TYPE_NAMES)
		def modules = contexts.collect { moduleDefCtx ->
			def typeCollector = new TypeCollectorVisitor(typeNames)
			typeCollector.visit(moduleDefCtx)
			def moduleDef = typeCollector.moduleDefinition
			if (moduleDef == null) {
				throw new AssertionError("No module defined")
			}
			return moduleDef
		}
		return new ModuleConfiguration(modules, typeNames)
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
		this.moduleDefinition = new ModuleDefinition(FQName.fromString(ctx.fqName.text), ctx)
		super.visitModuleDefinition(ctx)
		return null
	}

	@Override
	public Void visitTypeDefinition(SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def typeName = moduleDefinition.name.resolveLocalName(ctx.name.text)
		if (typeNames.contains(typeName)) {
			throw new IllegalStateException("Type already defined: ${typeName}")
		}
		typeNames.add(typeName)
		super.visitTypeDefinition(ctx)
		return null

	}
}
