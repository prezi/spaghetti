package com.prezi.gradle.spaghetti

import com.prezi.gradle.spaghetti.parse.ModuleDefinition
import com.prezi.gradle.spaghetti.parse.ModuleParser
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
		def moduleDef = new ModuleParser(definition.text).parse()

		def generator = project.getPlugins().getPlugin(SpaghettiPlugin).generators.get(platform)
		generateInternal(generator, moduleDef)
	}

	abstract protected void generateInternal(Generator generator, ModuleDefinition moduleDef)

	void definition(Object file) {
		this.definition = project.file(file)
	}

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}
}
