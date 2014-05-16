package com.prezi.spaghetti.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputFiles

/**
 * Created by lptr on 18/11/13.
 */
class AbstractSpaghettiTask extends ConventionTask {

	ConfigurableFileCollection bundles = project.files()
	void bundles(Object... bundles) {
		this.bundles.from(*bundles)
	}
	void bundle(Object... bundles) {
		this.bundles(*bundles)
	}

	@InputFiles
	FileCollection getBundles() {
		return project.files(this.bundles)
	}

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}
}
