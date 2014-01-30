package com.prezi.spaghetti.gradle

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.SourceMap;

class Closure {

	private static CompilationLevel compilationLevel = CompilationLevel.ADVANCED_OPTIMIZATIONS;
	private static int lineLengthThreshold = 1;

	public static int compile(String jsFileName, Appendable obfuscated, String sourceMapName, Appendable sourceMap) {
		def compiler = new Compiler(System.err);
		def options = new CompilerOptions();

		def js = SourceFile.fromFile(jsFileName, UTF_8);

		compilationLevel.setOptionsForCompilationLevel(options);
		options.setLineLengthThreshold(lineLengthThreshold);
		options.setSourceMapOutputPath("dummy.map"); // This needs to be here only so that Closure generates sourcemap info

		def res = compiler.compile([], [js], options);

		def retCode = Math.min(res.errors.length, 0x7f);
		if (retCode != 0) {
			return retCode;
		}

		obfuscated.append(compiler.toSource());
		compiler.getSourceMap().appendTo(sourceMap, sourceMapName);

		return 0;
		
	}
}