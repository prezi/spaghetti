package com.prezi.spaghetti.obfuscation

import com.prezi.spaghetti.ReservedWords
import com.prezi.spaghetti.SourceMap
import com.prezi.spaghetti.SymbolCollectVisitor

/**
 * Created by lptr on 15/05/14.
 */
class ModuleObfuscator {
	private static final List<String> DEFAULT_PROTECTED_SYMBOLS = collectProtectedSymbols()
	private static Set<String> collectProtectedSymbols() {
		Set<String> symbols = [
		 		// RequireJS
				"define",
				// class definitions
				"prototype"
		]
		// Spaghetti reserved words
		symbols.addAll(ReservedWords.PROTECTED_WORDS)
		return symbols.asImmutable()
	}

	private final Set<String> protectedSymbols

	public ModuleObfuscator(Collection<String> protectedSymbols) {
		this.protectedSymbols = (DEFAULT_PROTECTED_SYMBOLS + protectedSymbols).asImmutable()
	}

	public ObfuscationResult obfuscateModule(ObfuscationParameters params) {
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


