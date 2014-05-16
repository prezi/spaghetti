package com.prezi.spaghetti

import groovy.transform.TupleConstructor

/**
 * Created by lptr on 15/05/14.
 */
class ModuleObfuscator {
	private static final List<String> protectedSymbols = collectProtectedSymbols()
	private static List<String> collectProtectedSymbols() {
		List<String> symbols = [
		 		// RequireJS
				"define",
				// class definitions
				"prototype",
				// Haxe class names -- Haxe likes to put this on global objects like Math and String and Date
				"__name__"
		]
		// Spaghetti reserved words
		symbols.addAll(ReservedWords.PROTECTED_WORDS)
		return symbols.asImmutable()
	}

	public static ObfuscationResult obfuscateModule(ObfuscationParameters params) {
		def config = params.config
		def module = params.module
		def modules = config.localModules + config.dependentModules
		Set<String> symbols = protectedSymbols + modules.collect {
			new SymbolCollectVisitor().visit(it.context)
		}.flatten() + (params.additionalSymbols ?: [])

		// OBFUSCATE
		def compressedJS = new StringBuilder();

		def workDir = params.workingDirectory
		workDir.delete() || workDir.deleteDir()
		workDir.mkdirs()
		def closureFile = new File(workDir, "closure.js");

		closureFile << params.javaScript << "\nvar __a = {}\n" + symbols.collect {
			"/** @expose */\n__a." + it + " = {}\n"
		}.join("");

		def sourceMapBuilder = new StringBuilder();
		def closureRet = ClosureCompiler.compile(closureFile.toString(), compressedJS, module.name, sourceMapBuilder, params.closureExterns)
		if (closureRet != 0) {
			throw new RuntimeException("Closure returned with exit code " + closureRet)
		}
		def mapJStoMin = sourceMapBuilder.toString();

		// SOURCEMAP
		def sourceMap = params.sourceMap
		def finalSourceMap;
		if (sourceMap) {
			finalSourceMap = SourceMap.compose(sourceMap, mapJStoMin, "module.map", params.nodeSourceMapRoot)
		} else {
			finalSourceMap = mapJStoMin;
		}

		def sourceMapRoot = params.sourceMapRoot
		if (sourceMapRoot) {
			finalSourceMap = SourceMap.relativizePaths(finalSourceMap, sourceMapRoot);
		}
		return new ObfuscationResult(
				javaScript: compressedJS.toString(),
				sourceMap: finalSourceMap
		)
	}
}

@TupleConstructor
public class ObfuscationParameters {
	ModuleConfiguration config
	ModuleDefinition module
	String javaScript
	String sourceMap
	URI sourceMapRoot
	String nodeSourceMapRoot
	Set<File> closureExterns
	Set<String> additionalSymbols
	File workingDirectory
}

@groovy.transform.Immutable
public class ObfuscationResult {
	String javaScript
	String sourceMap
}
