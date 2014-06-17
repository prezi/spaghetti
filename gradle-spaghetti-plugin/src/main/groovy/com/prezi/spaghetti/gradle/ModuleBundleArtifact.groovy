package com.prezi.spaghetti.gradle

import org.gradle.api.internal.artifacts.publish.AbstractPublishArtifact

class ModuleBundleArtifact extends AbstractPublishArtifact {
	String name
	String extension
	String type
	String classifier
	Date date
	File file

	private AbstractBundleModuleTask bundleTask

	public ModuleBundleArtifact(AbstractBundleModuleTask bundleTask) {
		super(bundleTask)
		this.bundleTask = bundleTask
	}

	public String getName() { name ?: "module" }

	public String getExtension() { extension ?: "zip" }

	public String getType() { type ?: "module" }

	public File getFile() { file ?: bundleTask.getOutputFile() }

	public Date getDate() { date ?: new Date(getFile().lastModified()) }
}
