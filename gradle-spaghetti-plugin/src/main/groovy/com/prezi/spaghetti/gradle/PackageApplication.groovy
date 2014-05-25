package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.packaging.ApplicationPackageParameters
import com.prezi.spaghetti.packaging.ApplicationPackager
import com.prezi.spaghetti.packaging.ApplicationType
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class PackageApplication extends AbstractPlatformAwareSpaghettiTask {

	@Input
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

	@Input
	ApplicationType type = ApplicationPackageParameters.DEFAULT_APPLICATION_TYPE
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
			default:
				throw new IllegalArgumentException("Unknown application type: ${type}")
		}
	}

	@Input
	boolean execute = ApplicationPackageParameters.DEFAULT_EXECUTE
	void execute(boolean execute) {
		this.execute = execute
	}

	@OutputDirectory
	File outputDirectory
	def outputDirectory(Object outputDirectory) {
		this.outputDirectory = project.file(outputDirectory)
	}

	PackageApplication()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/application") }
	}

	@TaskAction
	makeBundle() {
		def bundles = lookupBundles()
		logger.info "Creating application in {}", getOutputDirectory()
		ApplicationPackager.packageApplicationDirectory(getOutputDirectory(), new ApplicationPackageParameters(
				bundles: bundles.allBundles,
				baseUrl: getBaseUrl(),
				applicationName: getApplicationName(),
				modulesDirectory: getModulesDirectory(),
				mainModule: getMainModule(),
				execute: getExecute(),
				type: getType()
		))
	}
}
