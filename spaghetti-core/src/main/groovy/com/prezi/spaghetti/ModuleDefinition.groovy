package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.ModuleDefinitionContext
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 15/11/13.
 */
class ModuleDefinition implements Scope {
	final FQName name
	final ModuleDefinitionContext context

	private final Set<String> localTypeNames
	private final Scope parentScope

	ModuleDefinition(ModuleDefinitionContext context, Scope parentScope)
	{
		this.name = FQName.fromContext(context.name)
		this.context = context
		this.parentScope = parentScope

		def localNames = new LinkedHashSet<String>()
		def typeCollector = new TypeCollectorVisitor(localNames)
		typeCollector.visit(context)
		this.localTypeNames = localNames.asImmutable()
	}

	@Override
	FQName resolveName(FQName unresolvedName) {
		if (!unresolvedName.hasNamespace() || unresolvedName.namespace == name.namespace)
		{
			if (localTypeNames.contains(unresolvedName.localName))
			{
				return name.qualifyLocalName(unresolvedName)
			}
		}

		return parentScope.resolveName(unresolvedName)
	}

	Collection<FQName> getTypeNames() {
		return localTypeNames.collect {
			name.qualifyLocalName(FQName.fromString(it))
		}
	}
}

private class TypeCollectorVisitor extends ModuleBaseVisitor<Void> {
	final Set<String> names

	TypeCollectorVisitor(Set<String> names) {
		this.names = names
	}

	@Override
	public Void visitTypeDefinition(@NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		registerTypeName(ctx.name.text)
		return null
	}

	@Override
	Void visitEnumDefinition(@NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		registerTypeName(ctx.name.text)
		return null
	}

	private void registerTypeName(String localName)
	{
		if (names.contains(localName))
		{
			throw new IllegalStateException("Type already defined: ${localName}")
		}
		names.add(localName)
	}
}
