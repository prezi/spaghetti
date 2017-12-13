package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.gradle.internal.ExternalDependencyAwareTask;
import com.prezi.spaghetti.obfuscation.CompilationLevel;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;

public class ClosureConcatenateTask extends SourceTask implements ExternalDependencyAwareTask {
	private File workDir;
	private File sourceDir;
	private Map<String, String> externalDependencies = Maps.newTreeMap();

	@Input
	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File dir) {
		workDir = dir;
	}

	@InputDirectory
	public File getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(File dir) {
		sourceDir = dir;
	}

	@OutputFile
	public File getOutputFile() {
		return new File(getWorkDir(), "concatenated.js");
	}

	@Input
	public Map<String, String> getExternalDependencies() {
		return externalDependencies;
	}
	public void externalDependencies(Map<String, String> externalDependencies) {
		this.externalDependencies.putAll(externalDependencies);
	}
	public void externalDependency(String importName, String dependencyName) {
		this.externalDependencies.put(importName, dependencyName);
	}
	public void externalDependency(String shorthand) {
		externalDependency(shorthand, shorthand);
	}

	@TaskAction
	public void concat() throws IOException, InterruptedException {
		File workDir = getWorkDir();
		FileUtils.deleteQuietly(workDir);

		File jsFilesDir = new File(workDir, "js");
		File nodeDir = new File(jsFilesDir, "node_modules");
		FileUtils.forceMkdir(nodeDir);

		FileUtils.copyDirectory(getSourceDir(), jsFilesDir);

		for (Map.Entry<String, String> extern : getExternalDependencies().entrySet()) {
			String varName = extern.getKey();
			String importName = extern.getValue();
			FileUtils.write(
				new File(nodeDir, importName + ".js"),
				String.format("module.exports = %s;\n", varName));
		}

		int exitValue = ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			FileUtils.listFiles(jsFilesDir, new String[] {"js"}, true),
			Sets.<File>newHashSet(),
			CompilationLevel.SIMPLE);

		if (exitValue != 0) {
			throw new RuntimeException("Closure Compiler return an error code: " + exitValue);
		}
	}
}
