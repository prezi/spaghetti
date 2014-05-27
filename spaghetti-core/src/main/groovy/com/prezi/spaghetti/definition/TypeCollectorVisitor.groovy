package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/05/14.
 */
class TypeCollectorVisitor extends ModuleBaseVisitor<Void> {
	private final Set<String> names
	private final Set<FQName> externs
	private final Map<String, FQName> imports

	TypeCollectorVisitor(Set<String> names, Set<FQName> externs, Map<String, FQName> imports) {
		this.names = names
		this.externs = externs
		this.imports = imports
	}

	@Override
	Void visitImportDeclaration(@NotNull @NotNull ModuleParser.ImportDeclarationContext ctx) {
		registerImport(FQName.fromContext(ctx.name), ctx.alias?.text);
		return null
	}

	@Override
	public Void visitInterfaceDefinition(@NotNull ModuleParser.InterfaceDefinitionContext ctx) {
		registerTypeName(ctx.name.text)
		return null
	}

	@Override
	Void visitEnumDefinition(@NotNull ModuleParser.EnumDefinitionContext ctx) {
		registerTypeName(ctx.name.text)
		return null
	}

	@Override
	Void visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx) {
		registerTypeName(ctx.name.text)
		return null
	}

	@Override
	Void visitExternTypeDefinition(@NotNull @NotNull ModuleParser.ExternTypeDefinitionContext ctx) {
		registerExternType(FQName.fromContext(ctx.name))
		return null
	}

	private void registerExternType(FQName name) {
		if (externs.contains(name)) {
			throw new IllegalStateException("Extern already defined: ${name}")
		}
		externs.add(name)
	}

	private void registerTypeName(String localName) {
		if (names.contains(localName)) {
			throw new IllegalStateException("Type already defined: ${localName}")
		}
		names.add(localName)
	}

	private void registerImport(FQName name, String alias) {
		def localName = alias ?: name.localName
		if (imports.containsKey(localName)) {
			throw new IllegalStateException("Import collision: ${localName}")
		}
		imports.put(localName, name)
	}
}
