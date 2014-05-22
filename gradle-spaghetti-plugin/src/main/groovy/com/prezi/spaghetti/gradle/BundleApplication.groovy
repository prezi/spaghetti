package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ApplicationBundler
import com.prezi.spaghetti.bundle.ApplicationBundlerParameters
import com.prezi.spaghetti.bundle.Wrapper
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
				mainModule: getMainModule(),
				execute: getExecute(),
				wrapper: Wrapper.AMD
		))
	}
}
