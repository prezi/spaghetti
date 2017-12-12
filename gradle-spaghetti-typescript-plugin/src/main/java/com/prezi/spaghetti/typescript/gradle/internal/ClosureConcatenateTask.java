package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.obfuscation.CompilationLevel;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;

public class ClosureConcatenateTask extends SourceTask {
	private File workDir;
	private File sourceDir;

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

	@TaskAction
	public void concat() throws IOException, InterruptedException {
		File workDir = getWorkDir();
		FileUtils.deleteQuietly(workDir);
		FileUtils.forceMkdir(workDir);
		File jsFilesDir = new File(workDir, "js");
		FileUtils.forceMkdir(jsFilesDir);
		FileUtils.copyDirectory(getSourceDir(), jsFilesDir);


		ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			FileUtils.listFiles(jsFilesDir, new String[] {"js"}, true),
			Sets.<File>newHashSet(),
			CompilationLevel.SIMPLE);
	}
}
