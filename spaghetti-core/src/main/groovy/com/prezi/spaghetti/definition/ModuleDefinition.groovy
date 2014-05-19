package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleParser.ModuleDefinitionContext

/**
 * Created by lptr on 15/11/13.
 */
class ModuleDefinition implements Scope, Comparable<ModuleDefinition> {
	final String name
	final ModuleType type
	final String alias
	final ModuleDefinitionContext context
	final String definitionSource

	private final Set<String> localTypeNames
	private final Set<FQName> externs
	private final Map<String, FQName> imports
	private final Scope parentScope

	ModuleDefinition(String definitionSource, ModuleDefinitionContext context, Scope parentScope)
	{
		this.type = context.isStatic != null ? ModuleType.STATIC : ModuleType.DYNAMIC
		this.name = context.name.text
		this.alias = context.alias ? context.alias.text : context.name.text.split(/\./).last().capitalize()
		this.context = context
		this.definitionSource = definitionSource
		this.parentScope = parentScope

		Set<String> localNames = []
		Set<FQName> externs = []
		Map<String, FQName> imports = [:]
		def typeCollector = new TypeCollectorVisitor(localNames, externs, imports)
		typeCollector.visit(context)
		this.localTypeNames = localNames.asImmutable()
		this.externs = externs.asImmutable()
		this.imports = imports.asImmutable()
	}

	@Override
	FQName resolveName(FQName unresolvedName) {
		if (!unresolvedName.hasNamespace())
		{
			if (imports.containsKey(unresolvedName.localName))
			{
				unresolvedName = imports.get(unresolvedName.localName)
			}
		}

		if (!unresolvedName.hasNamespace() || unresolvedName.namespace == name)
		{
			if (localTypeNames.contains(unresolvedName.localName))
			{
				return FQName.fromString(name, unresolvedName.localName)
			}
		}

		if (externs.contains(unresolvedName)) {
			return resolveExtern(unresolvedName)
		}

		return parentScope.resolveName(unresolvedName)
	}

	@Override
	FQName resolveExtern(FQName name)
	{
		return parentScope.resolveExtern(name)
	}

	Collection<FQName> getTypeNames() {
		return localTypeNames.collect {
			FQName.qualifyLocalName(name, FQName.fromString(it))
		}
	}

	@Override
	int compareTo(ModuleDefinition o) {
		return name.compareTo(o.name)
	}

	@Override
	String toString() {
		return "Module ${name}"
	}
}

