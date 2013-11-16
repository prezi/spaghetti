package com.prezi.gradle.spaghetti

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
abstract class AbstractGenerateTask extends DefaultTask {

	String platform

	@InputFile
	File definition

	@OutputDirectory
	File outputDirectory

	@TaskAction
	public void generate() {
		def moduleDefCtx = ModuleParser.parse(definition.text)
		def config = ModuleConfigurationParser.parse(moduleDefCtx)

		def generator = project.getPlugins().getPlugin(SpaghettiPlugin).generators.get(platform)
		generateInternal(generator, config)
	}

	abstract protected void generateInternal(Generator generator, ModuleConfiguration config)

	void definition(Object file) {
		this.definition = project.file(file)
	}

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}
}
