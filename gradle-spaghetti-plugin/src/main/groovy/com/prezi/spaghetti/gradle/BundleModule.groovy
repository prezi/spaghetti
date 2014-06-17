package com.prezi.spaghetti.gradle
class BundleModule extends AbstractBundleModuleTask {

	BundleModule()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/bundle") }
	}
}
