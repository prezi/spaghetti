package com.prezi.spaghetti.obfuscation;

import com.google.common.collect.ImmutableSet;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;
import com.prezi.spaghetti.obfuscation.internal.SourceMap;
import com.prezi.spaghetti.obfuscation.internal.SymbolCollectVisitor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

/**
 * Obfuscates a module's code with Google Closure Compiler.
 */
public class ModuleObfuscator {
	private static final Set<String> DEFAULT_PROTECTED_SYMBOLS = collectProtectedSymbols();
	private final Set<String> protectedSymbols;

	private static Set<String> collectProtectedSymbols() {
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		builder.add("prototype");
		return builder.build();
	}

	/**
	 * Creates an obfuscator that will protect the given symbols.
	 *
	 * @param protectedSymbols the symbols to protect.
	 */
	public ModuleObfuscator(Collection<String> protectedSymbols) {
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		builder.addAll(DEFAULT_PROTECTED_SYMBOLS);
		builder.addAll(protectedSymbols);
		this.protectedSymbols = builder.build();
	}

	/**
	 * Obfuscates a module and updates its source map (if any).
	 *
	 * @param params the obfuscation parameters.
	 * @return the obfuscated JavaScript with the updated source map.
	 */
	public ObfuscationResult obfuscateModule(ObfuscationParameters params) throws IOException {
		ModuleConfiguration config = params.config;
		ModuleNode module = params.module;

		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		builder.addAll(protectedSymbols);
		for (ModuleNode moduleNode : config.getAllModules()) {
			builder.addAll(new SymbolCollectVisitor().visit(moduleNode));
		}

		builder.addAll(params.additionalSymbols);
		ImmutableSet<String> symbols = builder.build();

		// OBFUSCATE
		StringBuilder compressedJS = new StringBuilder();

		File workDir = params.workingDirectory;
		FileUtils.deleteQuietly(workDir);
		FileUtils.forceMkdir(workDir);
		File closureFile = new File(workDir, "closure.js");
		FileUtils.write(closureFile, params.javaScript);
		FileUtils.write(closureFile, "\nvar __a = {}\n", true);
		for (String symbol : symbols) {
			FileUtils.write(closureFile, "/** @expose */\n__a." + symbol + " = {}\n", true);
		}

		StringBuilder sourceMapBuilder = new StringBuilder();
		Integer closureRet = ClosureCompiler.compile(closureFile.toString(), compressedJS, module.getName(), sourceMapBuilder, params.closureExterns);
		if (closureRet != 0) {
			throw new RuntimeException("Closure returned with exit code " + closureRet);
		}

		String mapJStoMin = sourceMapBuilder.toString();

		// SOURCEMAP
		String sourceMap = params.sourceMap;
		Object finalSourceMap;
		if (sourceMap != null) {
			try {
				finalSourceMap = SourceMap.compose(sourceMap, mapJStoMin, "module.map", params.nodeSourceMapRoot);
			} catch (InterruptedException ex) {
				throw new IOException(ex);
			}
		} else {
			finalSourceMap = mapJStoMin;
		}

		URI sourceMapRoot = params.sourceMapRoot;
		if (sourceMapRoot != null) {
			finalSourceMap = SourceMap.relativizePaths((String) finalSourceMap, sourceMapRoot);
		}

		return new ObfuscationResult(compressedJS.toString(), (String) finalSourceMap);
	}
}
