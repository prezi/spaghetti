package com.prezi.spaghetti.obfuscation;

import com.google.common.collect.ImmutableSortedSet;
import com.google.javascript.jscomp.CompilationLevel;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.definition.ModuleConfiguration;

import java.io.File;
import java.net.URI;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;

/**
 * Parameter object for obfuscation.
 */
public class ObfuscationParameters {
	public final ModuleConfiguration config;
	public final ModuleNode module;
	public final String javaScript;
	public final String sourceMap;
	public final URI sourceMapRoot;
	public final String nodeSourceMapRoot;
	public final SortedSet<File> closureExterns;
	public final SortedSet<String> additionalSymbols;
	public final File workingDirectory;
	public final File tsCompilerPath;
	public final Logger logger;
	public final CompilationLevel compilationLevel;

	public ObfuscationParameters(ModuleConfiguration config, ModuleNode module, String javaScript, String sourceMap, URI sourceMapRoot, String nodeSourceMapRoot, Set<File> closureExterns, Set<String> additionalSymbols, File workingDirectory, File tsCompilerPath, Logger logger, CompilationLevel compilationLevel) {
		this.config = config;
		this.module = module;
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
		this.sourceMapRoot = sourceMapRoot;
		this.nodeSourceMapRoot = nodeSourceMapRoot;
		this.closureExterns = ImmutableSortedSet.copyOf(closureExterns);
		this.additionalSymbols = ImmutableSortedSet.copyOf(additionalSymbols);
		this.workingDirectory = workingDirectory;
		this.tsCompilerPath = tsCompilerPath;
		this.logger = logger;
		this.compilationLevel = compilationLevel;
	}

	public ObfuscationParameters(ModuleConfiguration config, ModuleNode module, String javaScript, String sourceMap, URI sourceMapRoot, String nodeSourceMapRoot, Set<File> closureExterns, Set<String> additionalSymbols, File workingDirectory, File tsCompilerPath, Logger logger, String compilationLevel) {
		this(config, module, javaScript, sourceMap, sourceMapRoot, nodeSourceMapRoot, closureExterns, additionalSymbols, workingDirectory, tsCompilerPath, logger, convertCompilationLevel(compilationLevel));
	}

	private static CompilationLevel convertCompilationLevel(String compilationLevel) {
		if (compilationLevel.equals("advanced")) {
			return CompilationLevel.ADVANCED_OPTIMIZATIONS;
		} else if (compilationLevel.equals("simple")) {
			return CompilationLevel.SIMPLE_OPTIMIZATIONS;
		} else if (compilationLevel.equals("whitespace")) {
			return CompilationLevel.WHITESPACE_ONLY;
		} else {
			throw new IllegalArgumentException("Unknown compilation level: " + compilationLevel);
		}
	}
}
