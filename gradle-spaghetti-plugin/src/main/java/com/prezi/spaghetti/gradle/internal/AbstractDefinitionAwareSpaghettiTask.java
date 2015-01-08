package com.prezi.spaghetti.gradle.internal;

import org.gradle.api.tasks.InputFile;

import java.io.File;

public class AbstractDefinitionAwareSpaghettiTask extends AbstractLanguageAwareSpaghettiTask implements DefinitionAwareSpaghettiTask {
	private File definition;

	@InputFile
	public File getDefinition() {
		return definition;
	}

	public void setDefinition(Object definition) {
		this.definition = getProject().file(definition);
	}

	public void definition(Object definition) {
		setDefinition(definition);
	}
}
