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
	public static ModuleConfiguration parse(
			Collection<ModuleDefinitionSource> dependentModuleSources,
			Collection<ModuleDefinitionSource> localModuleSources,
			Map<FQName, FQName> externs,
			String version,
			String sourceBaseUrl) {
		def globalScope = new GlobalScope(externs)
		def modules = dependentModuleSources.collect { moduleSource ->
			return parseModule(moduleSource, version, sourceBaseUrl, globalScope)
		}
		def localModules = localModuleSources.collect { moduleSource ->
			return parseModule(moduleSource, version, sourceBaseUrl, globalScope)
		}
		return new ModuleConfiguration(modules + localModules, localModules, globalScope)
	}

	private static ModuleDefinition parseModule(ModuleDefinitionSource source, String version, String sourceBaseUrl, GlobalScope globalScope)
	{
		def module = new ModuleDefinition(source.contents, parse(source), version, sourceBaseUrl, globalScope)
		globalScope.registerNames(module.typeNames)
		return module
	}

	public static ModuleDefinitionContext parse(ModuleDefinitionSource source) {
		def input = new ANTLRInputStream(source.contents)
		def lexer = new ModuleLexer(input)
		def tokens = new CommonTokenStream(lexer)
		def parser = new ModuleParser(tokens)
		parser.removeErrorListeners()
		parser.addErrorListener(new ParserErrorListener(source.location))
		def tree = parser.moduleDefinition()
		if (parser.numberOfSyntaxErrors > 0) {
			throw new IllegalArgumentException("Could not parse module definition '${source.location}', see errors reported above")
		}
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
