package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.definition.DefinitionFile;
import org.gradle.api.Task;

import java.util.concurrent.Callable;

public class DefinitionOverride {
	private Callable<DefinitionFile> definitionFile;
	private Task generatorTask;

	public DefinitionOverride(Callable<DefinitionFile> definitionFile, Task generatorTask) {
		this.definitionFile = definitionFile;
		this.generatorTask = generatorTask;
	}

	public Callable<DefinitionFile> getDefinitionFile() {
		return definitionFile;
	}

	public Task getGeneratorTask() {
		return generatorTask;
	}
}
