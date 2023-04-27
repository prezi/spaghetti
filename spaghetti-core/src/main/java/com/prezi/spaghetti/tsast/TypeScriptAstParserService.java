package com.prezi.spaghetti.tsast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

public class TypeScriptAstParserService {
	public static Set<String> collectExportedSymbols(File workDir, File tsCompilerPath, String tsContent, Logger logger) throws IOException, InterruptedException {
		List<String> output = executeTsApiParserWithContent(
			logger,
			workDir,
			tsCompilerPath,
			"--collectExportedIdentifiers",
			tsContent);

		return Sets.newHashSet(Splitter.on(',').split(output.get(0).trim()));
	}

	public static List<String> verifyModuleDefinition(File workDir, File tsCompilerPath, File definitionFile, Logger logger) throws IOException, InterruptedException {
		List<String> output = executeTsApiParser(
			logger,
			workDir,
			tsCompilerPath,
			"--verifyModuleDefinition",
			definitionFile,
			null);

		return output;
	}

	public static List<String> verifyLazyModuleDefinition(File workDir, File tsCompilerPath, File definitionFile, Logger logger) throws IOException, InterruptedException {
		List<String> output = executeTsApiParser(
			logger,
			workDir,
			tsCompilerPath,
			"--verifyLazyModuleDefinition",
			definitionFile,
			null);

		return output;
	}

	public static List<String> mergeDefinitionFileImports (File workDir, File tsCompilerPath, File definitionFile, File outputFile, Logger logger) throws IOException, InterruptedException {
		List<String> output = executeTsApiParser(
			logger,
			workDir,
			tsCompilerPath,
			"--mergeDtsForJs",
			definitionFile,
			outputFile);

		return output;
	}

	private static List<String> executeTsApiParserWithContent(Logger logger, File workDir, File tsCompilerPath, String param, String tsContent) throws IOException, InterruptedException {
		File definitionFile = new File(workDir, "definition.d.ts");
		FileUtils.write(definitionFile, tsContent);
		return executeTsApiParser(logger, workDir, tsCompilerPath, param, definitionFile, null);
	}

	private static List<String> executeTsApiParser(Logger logger, File workDir, File tsCompilerPath, String param, File definitionFile, File outputFile) throws IOException, InterruptedException {

		File tsAstParser = new File(workDir, "tsAstParser.js");
		FileUtils.copyURLToFile(
			Resources.getResource(TypeScriptAstParserService.class, "/tsAstParser.js"),
			tsAstParser);

		List<String> command = new ArrayList<String>();
		command.add("node");
		command.add(tsAstParser.getAbsolutePath());
		command.add(param);
		command.add(definitionFile.getAbsolutePath());
		if (outputFile != null) {
			command.add(outputFile.getAbsolutePath());
		}
		String nodePath = tsCompilerPath.getParentFile().getAbsolutePath();
		return executeCommand(command, nodePath, logger);
	}

	private static List<String> executeCommand(List<String> command, String nodePath, Logger logger) throws IOException, InterruptedException {
		try {
			logger.info("Executing: NODE_PATH={} {}", nodePath, Joiner.on(" ").join(command));
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.environment().put("NODE_PATH", nodePath);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			List<String> lines = IOUtils.readLines(process.getInputStream());
			process.waitFor();
			if (process.exitValue() != 0) {
				throw new TypeScriptAstParserException("tsAstParser failure: " + process.exitValue(), lines);
			}
			return lines;
		} catch (IOException e) {
			throw new IOException("Cannot run node.", e);
		}
	}
}
