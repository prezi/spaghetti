package com.prezi.spaghetti.obfuscation.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.prezi.spaghetti.obfuscation.ClosureTarget;
import com.prezi.spaghetti.obfuscation.CompilationLevel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Collection;

public class ClosureCompiler {

	private static Logger logger = LoggerFactory.getLogger(ClosureCompiler.class);

	private static void add(List<String> args, String a, String b) {
		args.add(a);
		args.add(b);
	}

	public static int minify(
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
			ClosureTarget target
	) throws IOException, InterruptedException   {
		File jarPath = copyJarFile(workDir, "/closure-compiler-wrapper.jar");
		List<String> args = Lists.newArrayList();
		args.add("java");
		add(args, "-jar", jarPath.getAbsolutePath());
		if (target.equals(ClosureTarget.ES5)) {
			args.add("--es5");
		}

		args.add("--concat");
		add(args, "--entry_point", entryPoint.getPath());
		add(args, "--js_output_file", outputFile.getPath());

		for (File inputSource : inputSources) {
			add(args, "--js", inputSource.getPath());
		}

		for (File customExtern : customExterns) {
			add(args, "--externs", customExtern.getPath());
		}

		logger.info("In working directory: {}", workDir.getPath());
		logger.info("Executing: {}", Joiner.on(" ").join(args));
		Process process = new ProcessBuilder(args)
			.directory(workDir)
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
