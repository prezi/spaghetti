package com.prezi.spaghetti.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles

class AbstractDefinitionAwareSpaghettiTask extends AbstractPlatformAwareSpaghettiTask {
	ConfigurableFileCollection definitions = project.files()
	public void definition(Object... definitions)
	{
		this.definitions.from(*definitions)
	}

	@InputFiles
	public FileCollection getDefinitions() {
		return project.files(this.definitions)
	}
}
