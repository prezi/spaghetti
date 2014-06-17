package com.prezi.spaghetti.obfuscation;

import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.config.ModuleConfiguration;

import java.io.File;
import java.net.URI;
import java.util.Set;

public class ObfuscationParameters {
	public final ModuleConfiguration config;
	public final ModuleNode module;
	public final String javaScript;
	public final String sourceMap;
	public final URI sourceMapRoot;
	public final String nodeSourceMapRoot;
	public final Set<File> closureExterns;
	public final Set<String> additionalSymbols;
	public final File workingDirectory;

	public ObfuscationParameters(ModuleConfiguration config, ModuleNode module, String javaScript, String sourceMap, URI sourceMapRoot, String nodeSourceMapRoot, Set<File> closureExterns, Set<String> additionalSymbols, File workingDirectory) {
		this.config = config;
		this.module = module;
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
		this.sourceMapRoot = sourceMapRoot;
		this.nodeSourceMapRoot = nodeSourceMapRoot;
		this.closureExterns = closureExterns;
		this.additionalSymbols = additionalSymbols;
		this.workingDirectory = workingDirectory;
	}
}
