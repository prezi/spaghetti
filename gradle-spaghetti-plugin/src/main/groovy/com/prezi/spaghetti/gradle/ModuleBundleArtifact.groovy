package com.prezi.spaghetti.gradle

import org.gradle.api.internal.artifacts.publish.AbstractPublishArtifact

/**
 * Created by lptr on 11/02/14.
 */
class ModuleBundleArtifact extends AbstractPublishArtifact {
	String name
	String extension
	String type
	String classifier
	Date date
	File file

	private InputOutput bundleTask

	public ModuleBundleArtifact(InputOutput bundleTask) {
		super(bundleTask)
		this.bundleTask = bundleTask
	}

	public String getName() { name ?: "module" }

	public String getExtension() { extension ?: "zip" }

	public String getType() { type ?: "module" }

	public File getFile() { file ?: bundleTask.getOutputFile() }

	public Date getDate() { date ?: new Date(getFile().lastModified()) }
}
