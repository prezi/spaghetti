package com.prezi.spaghetti.obfuscation.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.prezi.spaghetti.obfuscation.ClosureTarget;
import com.prezi.spaghetti.obfuscation.CompilationLevel;

import org.apache.commons.io.FileUtils;
// import com.google.javascript.jscomp.CheckLevel;
// import com.google.javascript.jscomp.CommandLineRunner;
// import com.google.javascript.jscomp.Compiler;
// import com.google.javascript.jscomp.CompilerOptions;
// import com.google.javascript.jscomp.DiagnosticGroups;
// import com.google.javascript.jscomp.Result;
// import com.google.javascript.jscomp.SourceFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Collection;

/**
 * lineLengthThreshold = 1 forces closure to insert a newline in the
 * obfuscated code every chance it gets.
 * <p>
 * The reason we want this is because column number information in
 * stack traces are inconsistent across browsers. Most of the time
 * we only have line numbers, which means we cannot exactly tell
 * where in the code the obfuscated statement in question
 * originates from.
 * </p>
 * <p>
 * This way we don't rely on support for column number information
 * in browsers.
 * </p>
 */
public class ClosureCompiler {

	private static Logger logger = LoggerFactory.getLogger(ClosureCompiler.class);

	private static void add(List<String> args, String a, String b) {
		args.add(a);
		args.add(b);
	}

	public static int compile(
			File workDir,
			File inputFile,
			File outputFile,
			File outputSourceMapFile,
			CompilationLevel compilationLevel,
			Collection<File> customExterns,
			ClosureTarget target
	) throws IOException, InterruptedException {

		File jarPath = copyJarFile(workDir, "/closure-compiler/closure-compiler-v20180204.jar");

		List<String> args = Lists.newArrayList();
		args.add("java");
		add(args, "-jar", jarPath.getAbsolutePath());

		add(args, "--compilation_level", compilationLevel.name());

		if (target.equals(ClosureTarget.ES5)) {
			add(args, "--language_in", "ECMASCRIPT5");
		} else if (target.equals(ClosureTarget.ES6)) {
			add(args, "--language_in", "ECMASCRIPT6");
			add(args, "--language_out", "NO_TRANSPILE");
			add(args, "--emit_use_strict", "false");
			add(args, "--rewrite_polyfills", "false");
		}

		add(args, "--jscomp_off", "checkVars");
		add(args, "--jscomp_off", "checkTypes");

		for (File customExtern : customExterns) {
			add(args, "--externs", customExtern.getAbsolutePath());
		}
		add(args, "--env", "BROWSER");
		add(args, "--js", inputFile.getAbsolutePath());
		add(args, "--create_source_map", outputSourceMapFile.getAbsolutePath());
		add(args, "--js_output_file", outputFile.getAbsolutePath());

		logger.info("Executing: {}", Joiner.on(" ").join(args));
		Process process = new ProcessBuilder(args)
			.redirectErrorStream(true)
			.start();

		String output = IOUtils.toString(process.getInputStream());

		int retCode = process.waitFor();
		if (retCode != 0) {
			logger.error("Obfuscation error:" + output);
		}
		return retCode;
	}

	public static int concat(
			File workDir,
			File outputFile,
			File entryPoint,
			Collection<File> inputSources,
			Collection<File> customExterns,
			CompilationLevel compilationLevel,
			ClosureTarget target
	) throws IOException, InterruptedException   {
		File jarPath = copyJarFile(workDir, "/closure-compiler-wrapper.jar");
		List<String> args = Lists.newArrayList();
		args.add("java");
		add(args, "-jar", jarPath.getAbsolutePath());
		if (target.equals(ClosureTarget.ES5)) {
			add(args, "--target", "ES5");
		}
		add(args, "--entry_point", entryPoint.getAbsolutePath());
		add(args, "--js_output_file", outputFile.getAbsolutePath());

		for (File inputSource : inputSources) {
			add(args, "--js", inputSource.getAbsolutePath());
		}

		for (File customExtern : customExterns) {
			add(args, "--externs", customExtern.getAbsolutePath());
		}

		logger.info("Executing: {}", Joiner.on(" ").join(args));
		Process process = new ProcessBuilder(args)
			.redirectErrorStream(true)
			.start();
		String output = IOUtils.toString(process.getInputStream());

		int retCode = process.waitFor();
		if (retCode != 0) {
			logger.error("ERROR: " + output);
		}
		return retCode;
	}

	private static File copyJarFile(File workDir, String resourceName) throws IOException {
		File jarPath = new File(workDir, "closure.jar");
		FileUtils.copyURLToFile(
			Resources.getResource(ClosureCompiler.class, resourceName),
			jarPath
		);

		return jarPath;
	}
}
