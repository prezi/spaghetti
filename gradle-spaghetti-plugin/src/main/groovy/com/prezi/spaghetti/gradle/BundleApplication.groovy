package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ApplicationBundler
import com.prezi.spaghetti.bundle.ApplicationBundlerParameters
import com.prezi.spaghetti.bundle.ApplicationType
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class BundleApplication extends AbstractPlatformAwareSpaghettiTask {

	@Input
	String mainModule
	void mainModule(String mainModule) {
		this.mainModule = mainModule
	}

	@Input
	String baseUrl = ApplicationBundlerParameters.DEFAULT_BASE_URL
	void baseUrl(String baseUrl) {
		this.baseUrl = baseUrl
	}

	@Input
	String applicationName = ApplicationBundlerParameters.DEFAULT_APPLICATION_NAME
	void applicationName(String applicationName) {
		this.applicationName = applicationName
	}

	@Input
	String modulesDirectory = ApplicationBundlerParameters.DEFAULT_MODULES_DIRECTORY
	void modulesDirectory(String directory) {
		this.modulesDirectory = directory
	}

	@Input
	ApplicationType type = ApplicationBundlerParameters.DEFAULT_APPLICATION_TYPE
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
	boolean execute = ApplicationBundlerParameters.DEFAULT_EXECUTE
	void execute(boolean execute) {
		this.execute = execute
	}

	@OutputDirectory
	File outputDirectory
	def outputDirectory(Object outputDirectory) {
		this.outputDirectory = project.file(outputDirectory)
	}

	BundleApplication()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/application") }
	}

	@TaskAction
	makeBundle() {
		def bundles = lookupBundles()
		logger.info "Creating application in {}", getOutputDirectory()
		ApplicationBundler.bundleApplicationDirectory(getOutputDirectory(), new ApplicationBundlerParameters(
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
