package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ApplicationBundler
import com.prezi.spaghetti.bundle.ApplicationBundlerParameters
import com.prezi.spaghetti.bundle.Wrapper
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class BundleApplication extends AbstractPlatformAwareSpaghettiTask {

	@Input
	String mainModule

	@Input
	String baseUrl = ApplicationBundlerParameters.DEFAULT_BASE_URL

	@Input
	String applicationName = ApplicationBundlerParameters.DEFAULT_APPLICATION_NAME

	@Input
	boolean execute = ApplicationBundlerParameters.DEFAULT_EXECUTE

	def mainModule(Object mainModule) {
		this.mainModule = mainModule?.toString()
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
