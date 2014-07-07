package com.prezi.spaghetti.obfuscation;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * lineLengthThreshold = 1 forces closure to insert a newline in the
 * obfuscated code every chance it gets.
 * <p/>
 * The reason we want this is because column number information in
 * stack traces are inconsistent across browsers. Most of the time
 * we only have line numbers, which means we cannot exactly tell
 * where in the code the obfuscated statement in question
 * originates from.
 * <p/>
 * This way we don't rely on support for column number information
 * in browsers.
 */
public class ClosureCompiler {

	private static Logger logger = LoggerFactory.getLogger(ClosureCompiler.class);
	private static final int lineLengthThreshold = 1;
	private static final CompilationLevel compilationLevel = CompilationLevel.ADVANCED_OPTIMIZATIONS;

	/**
	 * compiles 'jsFileName', appends the obfuscated code to
	 * 'obfuscated' and appends the source map to 'sourceMap' using
	 * 'sourceMapName' (the 'file' field in the sourcemap)
	 */
	public static int compile(String jsFileName, Appendable obfuscated, String sourceMapName, Appendable sourceMap, Set<File> customExterns) throws IOException {
		com.google.javascript.jscomp.Compiler compiler = new Compiler(System.err);
		CompilerOptions options = new CompilerOptions();

		SourceFile js = SourceFile.fromFile(jsFileName, UTF_8);

		// OPTIONS
		compilationLevel.setOptionsForCompilationLevel(options);
		options.setLineLengthThreshold(lineLengthThreshold);
		// We need to set this so that Closure generates sourcemap info, it won't actually create a file.
		options.setSourceMapOutputPath("dummy.map");
		// ES5 relaxes some keywords
		options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5);

		// Set default externs so that commonly used primitives are protected
		List<SourceFile> externs = Lists.newArrayList(CommandLineRunner.getDefaultExterns());
		for (File customExtern : customExterns) {
			externs.add(SourceFile.fromFile(customExtern, UTF_8));
		}

		// COMPILE
		logger.info("Closure compile with externs: {}", externs);
		Result res = compiler.compile(externs, Arrays.asList(js), options);
		Integer retCode = Math.min(res.errors.length, 0x7f);
		if (retCode != 0) {
			return retCode;
		}

		// append results
		obfuscated.append(compiler.toSource());
		compiler.getSourceMap().appendTo(sourceMap, sourceMapName);

		return 0;
	}
}
