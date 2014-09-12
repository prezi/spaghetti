package com.prezi.spaghetti.gradle;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;

public class AbstractDefinitionAwareSpaghettiTask extends AbstractLanguageAwareSpaghettiTask {
	private ConfigurableFileCollection definitions = getProject().files();

	public void definition(Object... definitions) {
		this.definitions.from(definitions);
	}

	@InputFiles
	public FileCollection getDefinitions() {
		return getProject().files(this.definitions);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions = definitions;
	}
}
