package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.ModuleBundle.Elements
import org.gradle.api.Action
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails

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
	extract(IncrementalTaskInputs inputs) {
		def output = getOutputDirectory()
		output.mkdirs()

		// Remove all previously created module directories that don't belong to bundles
		def allBundleNames = getBundles()*.name
		output.eachDir { moduleDir ->
			if (!(moduleDir.name in allBundleNames)) {
				logger.debug "Removing ${moduleDir} because it does not belong to a module anymore"
				moduleDir.delete() || moduleDir.deleteDir()
			}
		}

		// Collect which bundles were changed/added
		def changedBundles = []
		inputs.outOfDate(new Action<InputFileDetails>() {
			@Override
			void execute(InputFileDetails details) {
				ExtractModules.this.logger.debug((details.isModified() ? "Re-extracting" : "Extracting") + " bundle file ${details.file}")
				changedBundles.add details.file
			}
		})

		ModuleDefinitionLookup.getAllBundles(changedBundles).each { ModuleBundle bundle ->
			logger.info "Extracting bundle ${bundle.name}"
			bundle.extract(new File(output, bundle.name), getElementsToExtract())
		}
	}
}
