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

		File jarPath = copyJarFile(workDir);
		List<String> args = Lists.newArrayList(
			"java", "-jar", jarPath.getAbsolutePath(),
			"--compilation_level", compilationLevel.name(),
			"--js", inputFile.getAbsolutePath(),
			"--create_source_map", outputSourceMapFile.getAbsolutePath(),
			"--js_output_file", outputFile.getAbsolutePath()
		);

		if (target.equals(ClosureTarget.ES5)) {
			args.add("--es5");
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
	) throws IOException, InterruptedException {

		File jarPath = copyJarFile(workDir);
		List<String> args = Lists.newArrayList(
			"java", "-jar", jarPath.getAbsolutePath(),
			"--concat",
			"--entry_point", entryPoint.getPath(),
			"--js_output_file", outputFile.getPath()
		);

		if (target.equals(ClosureTarget.ES5)) {
			args.add("--es5");
		}

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

	private static File copyJarFile(File workDir) throws IOException {
		File jarPath = new File(workDir, "closure-compiler-wrapper.jar");
		FileUtils.copyURLToFile(
			Resources.getResource(ClosureCompiler.class, "/closure-compiler-wrapper.jar"),
			jarPath
		);

		return jarPath;
	}
}
