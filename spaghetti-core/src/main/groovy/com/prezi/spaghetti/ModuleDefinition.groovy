package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.ModuleDefinitionContext
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 15/11/13.
 */
class ModuleDefinition implements Scope, Comparable<ModuleDefinition> {
	final FQName name
	final String version
	final String source
	final ModuleDefinitionContext context

	private final Set<String> localTypeNames
	private final Set<FQName> externs
	private final Map<String, FQName> imports
	private final Scope parentScope

	ModuleDefinition(ModuleDefinitionContext context, String version, String source, Scope parentScope)
	{
		this.name = FQName.fromContext(context.name)
		this.version = version
		this.source = source
		this.context = context
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

		if (!unresolvedName.hasNamespace() || unresolvedName.namespace == name.namespace)
		{
			if (localTypeNames.contains(unresolvedName.localName))
			{
				return name.qualifyLocalName(unresolvedName)
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
			name.qualifyLocalName(FQName.fromString(it))
		}
	}

	@Override
	int compareTo(ModuleDefinition o) {
		return name.fullyQualifiedName.compareTo(o.name.fullyQualifiedName)
	}

	@Override
	String toString() {
		return "Module ${name}"
	}
}

private class TypeCollectorVisitor extends ModuleBaseVisitor<Void> {
	private final Set<String> names
	private final Set<FQName> externs
	private final Map<String, FQName> imports

	TypeCollectorVisitor(Set<String> names, Set<FQName> externs, Map<String, FQName> imports) {
		this.names = names
		this.externs = externs
		this.imports = imports
	}

	@Override
	Void visitImportDeclaration(@NotNull @NotNull ModuleParser.ImportDeclarationContext ctx)
	{
		registerImport(FQName.fromContext(ctx.name), ctx.alias?.text);
		return null
	}

	@Override
	public Void visitInterfaceDefinition(@NotNull ModuleParser.InterfaceDefinitionContext ctx)
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

	@Override
	Void visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		registerTypeName(ctx.name.text)
		return null
	}

	@Override
	Void visitExternTypeDefinition(@NotNull @NotNull ModuleParser.ExternTypeDefinitionContext ctx)
	{
		registerExternType(FQName.fromContext(ctx.name))
		return null
	}

	private void registerExternType(FQName name)
	{
		if (externs.contains(name)) {
			throw new IllegalStateException("Extern already defined: ${name}")
		}
		externs.add(name)
	}

	private void registerTypeName(String localName)
	{
		if (names.contains(localName))
		{
			throw new IllegalStateException("Type already defined: ${localName}")
		}
		names.add(localName)
	}

	private void registerImport(FQName name, String alias)
	{
		def localName = alias ?: name.localName
		if (imports.containsKey(localName))
		{
			throw new IllegalStateException("Import collision: ${localName}")
		}
		imports.put(localName, name)
	}
}
