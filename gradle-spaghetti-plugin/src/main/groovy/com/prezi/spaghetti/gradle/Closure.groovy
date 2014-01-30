package com.prezi.spaghetti.gradle

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.SourceMap;
import com.google.javascript.jscomp.CommandLineRunner;

class Closure {

	/* This forces closure to insert a newline in the obfuscated code
	   every chance it gets.

	   The reason we want this is because column number information in
	   stack traces are inconsistent across browsers. Most of the time
	   we only have line numbers, which means we cannot exactly tell
	   where in the code the obfuscated statement in question
	   originates from.

	   This way we don't rely on support for column number information
	   in browsers.
	*/
	private static final int lineLengthThreshold = 1;
	private static CompilationLevel compilationLevel = CompilationLevel.ADVANCED_OPTIMIZATIONS;

	// compiles 'jsFileName', appends the obfuscated code to 'obfuscated' and appends the source map to 'sourceMap' using 'sourceMapName' (the 'file' field in the sourcemap)
	public static int compile(String jsFileName, Appendable obfuscated, String sourceMapName, Appendable sourceMap) {
		def compiler = new Compiler(System.err);
		def options = new CompilerOptions();

		def js = SourceFile.fromFile(jsFileName, UTF_8);

		// OPTIONS
		compilationLevel.setOptionsForCompilationLevel(options);
		options.setLineLengthThreshold(lineLengthThreshold);
		options.setSourceMapOutputPath("dummy.map"); // We need to set this so that Closure generates sourcemap info, it won't actually create a file.

		// Set default externs so that commonly used primitives are protected
		def externs = CommandLineRunner.getDefaultExterns();

		// COMPILE
		def res = compiler.compile(externs, [js], options);
		def retCode = Math.min(res.errors.length, 0x7f);
		if (retCode != 0) {
			return retCode;
		}

		// append results
		obfuscated.append(compiler.toSource());
		compiler.getSourceMap().appendTo(sourceMap, sourceMapName);

		return 0;
	}

}