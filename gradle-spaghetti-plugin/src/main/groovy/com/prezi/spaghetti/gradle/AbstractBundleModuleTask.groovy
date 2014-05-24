package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import com.prezi.spaghetti.bundle.ModuleBundleParameters
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 19/04/14.
 */
class AbstractBundleModuleTask extends AbstractDefinitionAwareSpaghettiTask {

	@InputFile
	File inputFile
	def inputFile(Object inputFile) {
		this.inputFile = project.file(inputFile)
	}

	@OutputDirectory
	File outputDirectory
	def outputDirectory(Object outputDirectory) {
		this.outputDirectory = project.file(outputDirectory)
	}

	@Input
	@Optional
	String sourceBaseUrl
	void sourceBaseUrl(String source) {
		this.sourceBaseUrl = source
	}

	File sourceMap
	void sourceMap(Object sourceMap) {
		this.sourceMap = project.file(sourceMap)
	}

	@InputFile
	@Optional
	File getSourceMap() {
		if (!sourceMap) {
			// This should probably be done with convention mapping
			def defSourceMap = new File(getInputFile().parentFile, getInputFile().name + ".map")
			if (defSourceMap.exists()) {
				sourceMap = defSourceMap
			}
		}
		return sourceMap
	}

	File resourcesDirectoryInternal
	void resourcesDirectory(Object resourcesDir) {
		this.resourcesDirectoryInternal = project.file(resourcesDir)
	}

	@InputDirectory
	@Optional
	File getResourcesDirectory() {
		def dir = getResourcesDirectoryInternal()
		return dir?.exists() ? dir : null
	}

	AbstractBundleModuleTask() {
		this.conventionMapping.inputFile = { new File(project.buildDir, "module.js") }
	}

	@TaskAction
	final bundle() {
		def moduleDefinitions = getDefinitions()
		if (moduleDefinitions.empty) {
			throw new IllegalArgumentException("No module definition present")
		}
		if (moduleDefinitions.files.size() > 1) {
			throw new IllegalArgumentException("Too many module definitions present: ${moduleDefinitions}")
		}
		def config = readConfig(moduleDefinitions)
		def module = config.getLocalModules().iterator().next()
		def processedJavaScript = createGenerator(config).processModuleJavaScript(module, config, getInputFile().text)

		createBundle(config, module, processedJavaScript, getSourceMap()?.text, getResourcesDirectory())
	}

	protected ModuleBundle createBundle(
			ModuleConfiguration config,
			ModuleDefinition module,
			String javaScript,
			String sourceMap,
			File resourceDir) {
		def outputDir = getOutputDirectory()
		logger.info "Creating bundle in ${outputDir}"
		ModuleBundleFactory.createDirectory(
				getOutputDirectory(),
				new ModuleBundleParameters(
						name: module.name,
						type: module.type,
						definition: module.definitionSource,
						version: String.valueOf(project.version),
						sourceBaseUrl: getSourceBaseUrl(),
						javaScript: javaScript,
						sourceMap: sourceMap,
						dependentModules: config.directDependentModules*.name,
						resourcesDirectory: resourceDir
				)
		)
	}
}
