package com.prezi.spaghetti.gradle.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.gradle.api.logging.Logger;

import com.google.common.base.Joiner;


import com.google.common.base.Splitter;


import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

public class TypeScriptAstParserService {
	public static Set<String> collectExportedSymbols(File workDir, File tsCompilerPath, String tsContent, Logger logger) throws IOException, InterruptedException {
		File definitionFile = new File(workDir, "definition.d.ts");
		FileUtils.write(definitionFile, tsContent);

		File tsAstParser = new File(workDir, "tsAstParser.js");
		FileUtils.copyURLToFile(
			Resources.getResource(TypeScriptAstParserService.class, "/tsAstParser.js"),
			tsAstParser);

		List<String> command = new ArrayList<String>();
		command.add("node");
		command.add(tsAstParser.getAbsolutePath());
		command.add("--collectExportedIdentifiers");
		command.add(definitionFile.getAbsolutePath());
		String nodePath = tsCompilerPath.getParentFile().getAbsolutePath();
		String output = executeCommand(command, nodePath, logger);

		return new HashSet<String>(Splitter.on(',').splitToList(output.trim()));
	}

	private static String executeCommand(List<String> command, String nodePath, Logger logger) throws IOException, InterruptedException {
		try {
			logger.info("Executing: NODE_PATH={} {}", nodePath, Joiner.on(" ").join(command));
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.environment().put("NODE_PATH", nodePath);
			Process process = processBuilder.start();

			ByteStreams.copy(process.getErrorStream(), System.out);
			BufferedReader reader =
				new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
			process.waitFor();
			if (process.exitValue() != 0) {
				throw new RuntimeException("TypeScript compilation failed: " + process.exitValue());
			}
			return builder.toString();
		} catch (IOException e) {
			throw new IOException("Cannot run tsc. Try installing it with\n\n\tnpm install -g typescript", e);
		}
	}
}