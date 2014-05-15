package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractBundleModuleTask {

	BundleModule()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/bundle") }
	}
}
