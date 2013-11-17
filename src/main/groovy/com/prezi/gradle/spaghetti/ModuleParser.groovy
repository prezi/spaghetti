package com.prezi.gradle.spaghetti

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import prezi.spaghetti.SpaghettiModuleLexer
import prezi.spaghetti.SpaghettiModuleParser
/**
 * Created by lptr on 15/11/13.
 */
class ModuleParser {
	public static SpaghettiModuleParser.ModuleDefinitionContext parse(String descriptor) {
		def input = new ANTLRInputStream(descriptor)
		return parseInternal(input)
	}

	public static SpaghettiModuleParser.ModuleDefinitionContext parse(File inputFile) {
		return inputFile.withReader("utf-8") { reader ->
			def input = new ANTLRInputStream(reader)
			return parseInternal(input)
		}
	}

	private static SpaghettiModuleParser.ModuleDefinitionContext parseInternal(ANTLRInputStream input) {
		def lexer = new SpaghettiModuleLexer(input)
		def tokens = new CommonTokenStream(lexer)
		def parser = new SpaghettiModuleParser(tokens)
		def tree = parser.moduleDefinition()
		return tree
	}
}
