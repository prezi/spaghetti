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
			Iterable<ModuleDefinitionContext> contexts, Iterable<ModuleDefinitionContext> localContexts, Map<FQName, FQName> externs, String version, String source) {
		def globalScope = new GlobalScope(externs)
		def modules = contexts.collect { context ->
			return parseModule(context, version, source, globalScope)
		}
		def localModules = localContexts.collect { context ->
			return parseModule(context, version, source, globalScope)
		}
		return new ModuleConfiguration(modules + localModules, localModules, globalScope)
	}

	private static ModuleDefinition parseModule(ModuleDefinitionContext context, String version, String source, GlobalScope globalScope)
	{
		def module = new ModuleDefinition(context, version, source, globalScope)
		globalScope.registerNames(module.typeNames)
		return module
	}

	public static ModuleDefinitionContext parse(String descriptor, String location) {
		def input = new ANTLRInputStream(descriptor)
		def lexer = new ModuleLexer(input)
		def tokens = new CommonTokenStream(lexer)
		def parser = new ModuleParser(tokens)
		parser.removeErrorListeners()
		parser.addErrorListener(new ParserErrorListener(location))
		def tree = parser.moduleDefinition()
		if (parser.numberOfSyntaxErrors > 0) {
			throw new IllegalArgumentException("Could not parse module definition '${location}', see errors reported above")
		}
		return tree
	}
}
