package com.prezi.spaghetti.gradle.internal;

import java.io.File;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;

import com.prezi.spaghetti.definition.DefinitionFile;

public class AbstractDefinitionAwareSpaghettiTask extends AbstractLanguageAwareSpaghettiTask implements DefinitionAwareSpaghettiTask {
	private DefinitionFile definition;

	@InputFile
	public File getDefinitionFile() {
		DefinitionFile d = getDefinition();
		if (d != null) {
			return d.getFile();
		}
		return null;
	}

	@Input
	public DefinitionFile getDefinition() {
		return definition;
	}

	public void setDefinition(DefinitionFile definition) {
		this.definition = definition;
	}

	public void definition(DefinitionFile definition) {
		setDefinition(definition);
	}
}
