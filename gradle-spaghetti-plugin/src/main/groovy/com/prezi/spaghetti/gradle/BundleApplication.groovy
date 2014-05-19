package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ApplicationBundler
import com.prezi.spaghetti.bundle.ApplicationBundlerParameters
import com.prezi.spaghetti.bundle.Wrapper
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class BundleApplication extends AbstractPlatformAwareSpaghettiTask {

	ConfigurableFileCollection applicationModules = project.files()
	void applicationModules(Object... applicationModules) {
		this.applicationModules.from(*applicationModules)
	}
	void applicationModule(Object... applicationModules) {
		this.applicationModules(applicationModules)
	}

	@InputFiles
	FileCollection getApplicationModules() {
		return project.files(this.applicationModules)
	}

	@Input
	String mainModule

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
		def bundles = ModuleBundleLookup.lookupFromConfiguration(getDependentModules())
		def appBundles = ModuleBundleLookup.lookup(getApplicationModules())
		ApplicationBundler.bundleApplicationDirectory(getOutputDirectory(), new ApplicationBundlerParameters(
				bundles: new TreeSet<>(bundles.allBundles + appBundles),
				mainModule: getMainModule(),
				wrapper: Wrapper.AMD
		))
	}
}
