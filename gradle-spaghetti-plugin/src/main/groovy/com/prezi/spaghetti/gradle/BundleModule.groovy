package com.prezi.spaghetti.gradle
/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractBundleModuleTask {

	BundleModule()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/bundle") }
	}
}
