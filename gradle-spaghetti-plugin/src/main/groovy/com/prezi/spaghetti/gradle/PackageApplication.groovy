package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.packaging.ApplicationPackageParameters
import com.prezi.spaghetti.packaging.ApplicationType
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class PackageApplication extends AbstractSpaghettiTask {

	@Input
	@Optional
	String mainModule
	void mainModule(String mainModule) {
		this.mainModule = mainModule
	}

	@Input
	String baseUrl = ApplicationPackageParameters.DEFAULT_BASE_URL
	void baseUrl(String baseUrl) {
		this.baseUrl = baseUrl
	}

	@Input
	String applicationName = ApplicationPackageParameters.DEFAULT_APPLICATION_NAME
	void applicationName(String applicationName) {
		this.applicationName = applicationName
	}

	@Input
	String modulesDirectory = ApplicationPackageParameters.DEFAULT_MODULES_DIRECTORY
	void modulesDirectory(String directory) {
		this.modulesDirectory = directory
	}

	@InputFiles
	ConfigurableFileCollection prefixes = project.files()
	void prefixes(Object... prefixes) {
		this.getPrefixes().from(*prefixes)
	}
	void prefix(Object... prefixes) {
		this.prefixes(prefixes)
	}

	@InputFiles
	ConfigurableFileCollection suffixes = project.files()
	void suffixes(Object... suffixes) {
		this.getSuffixes().from(*suffixes)
	}
	void suffix(Object... suffixes) {
		this.suffixes(suffixes)
	}

	@Input
	ApplicationType type = ApplicationType.COMMON_JS
	void type(String type) {
		switch (type.toUpperCase()) {
			case "AMD":
			case "REQUIREJS":
				this.type = ApplicationType.AMD
				break
			case "COMMONJS":
			case "NODE":
			case "NODEJS":
				this.type = ApplicationType.COMMON_JS
				break
			case "SINGLE":
			case "SINGLEFILE":
				this.type = ApplicationType.SINGLE_FILE
				break
			default:
				throw new IllegalArgumentException("Unknown application type: ${type}")
		}
	}

	@Input
	Boolean execute = null
	void execute(boolean execute) {
		this.execute = execute
	}
	Boolean getExecute() {
		return execute != null ? execute : mainModule != null
	}

	@OutputDirectory
	File outputDirectory
	def outputDirectory(Object outputDirectory) {
		this.outputDirectory = project.file(outputDirectory)
	}

	File getApplicationFile() {
		return new File(getOutputDirectory(), getApplicationName())
	}

	PackageApplication()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/application") }
		if (execute && !mainModule) {
			throw new IllegalArgumentException("You need to set mainModule as well when execute is true")
		}
	}

	@TaskAction
	makeBundle() {
		def bundles = lookupBundles()
		logger.info "Creating {} application in {}", getType().description, getOutputDirectory()
		getType().packager.packageApplicationDirectory(getOutputDirectory(), new ApplicationPackageParameters(
				bundles: bundles.allBundles,
				baseUrl: getBaseUrl(),
				applicationName: getApplicationName(),
				modulesDirectory: getModulesDirectory(),
				mainModule: getMainModule(),
				execute: getExecute(),
				prefixes: getPrefixes().files*.text,
				suffixes: getSuffixes().files*.text
		))
	}
}
