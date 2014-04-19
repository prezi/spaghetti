package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle.Elements
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 17/11/13.
 */
class ExtractModules extends AbstractSpaghettiTask {
	@OutputDirectory
	File outputDirectory

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}

	@Input
	EnumSet<Elements> elementsToExtract

	void extract(Elements... elements) {
		elementsToExtract = EnumSet.of(*elements)
	}

	void extract(String... elements) {
		elementsToExtract = EnumSet.of(*elements.collect { Elements.valueOf(it) })
	}

	ExtractModules() {
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/modules") }
		this.conventionMapping.elementsToExtract = { EnumSet.of(Elements.javascript, Elements.resources) }
	}

	@TaskAction
	extract() {
		def output = getOutputDirectory()
		output.delete() || output.deleteDir()
		output.mkdirs()
		logger.warn ">>>>> Bundles ${getBundles()}"
		ModuleDefinitionLookup.getAllBundles(getBundles()).each { bundle ->
			bundle.extract(new File(output, bundle.name), getElementsToExtract())
		}
	}
}
