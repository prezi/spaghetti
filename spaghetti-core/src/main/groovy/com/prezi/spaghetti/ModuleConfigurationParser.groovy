package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleLexer
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.ModuleDefinitionContext
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfigurationParser {
	public static ModuleConfiguration parse(Iterable<ModuleDefinitionContext> contexts,
											Iterable<ModuleDefinitionContext> localContexts,
											Map<FQName, FQName> externs) {
		def globalScope = new GlobalScope(externs)
		def modules = contexts.collect { context ->
			return parseModule(context, globalScope)
		}
		def localModules = localContexts.collect { context ->
			return parseModule(context, globalScope)
		}
		return new ModuleConfiguration(modules + localModules, localModules, globalScope)
	}

	private static ModuleDefinition parseModule(ModuleDefinitionContext context, GlobalScope globalScope)
	{
		def module = new ModuleDefinition(context, globalScope)
		globalScope.registerNames(module.typeNames)
		return module
	}

	public static ModuleDefinitionContext parse(String descriptor) {
		def input = new ANTLRInputStream(descriptor)
		def lexer = new ModuleLexer(input)
		def tokens = new CommonTokenStream(lexer)
		def parser = new ModuleParser(tokens)
		def tree = parser.moduleDefinition()
		return tree
	}
}

class GlobalScope implements Scope {

	private final Set<FQName> names = []
	private final Map<FQName, FQName> externs

	GlobalScope(Map<FQName, FQName> externs) {
		this.externs = externs
	}

	@Override
	FQName resolveName(FQName name)
	{
		if (!names.contains(name)) {
			throw new IllegalStateException("Name not found: ${name}, names registered: ${names}")
		}
		return name
	}

	@Override
	FQName resolveExtern(FQName name)
	{
		if (externs.containsKey(name)) {
			return externs.get(name)
		}
		else {
			// If we have no mapping for the extern, use the defined name
			return name
		}
	}

	void registerNames(Collection<FQName> names) {
		names.each {
			if (this.names.contains(it)) {
				throw new IllegalStateException("Global name registered multiple times: ${it}")
			}
		}
		this.names.addAll(names)
	}
}
