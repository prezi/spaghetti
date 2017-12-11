package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.obfuscation.CompilationLevel;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;

public class ClosureConcatenateTask extends SourceTask {
	private File workDir;

	@Input
	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File dir) {
		workDir = dir;
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

		ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			getSource().getFiles(),
			Sets.<File>newHashSet(),
			CompilationLevel.SIMPLE);
	}
}