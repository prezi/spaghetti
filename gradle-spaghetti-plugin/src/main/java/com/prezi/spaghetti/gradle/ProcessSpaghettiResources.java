package com.prezi.spaghetti.gradle;

import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

import java.io.File;

public class ProcessSpaghettiResources extends Copy {
	@Override
	protected void copy() {
		Project project = getProject();
		String prefix = getDestinationDir().getAbsolutePath() + File.separator;
		for (File file : getOutputs().getPreviousOutputFiles()) {
			if (file.getAbsolutePath().startsWith(prefix)) {
				project.delete(file);
			}
		}
		super.copy();
	}
}
