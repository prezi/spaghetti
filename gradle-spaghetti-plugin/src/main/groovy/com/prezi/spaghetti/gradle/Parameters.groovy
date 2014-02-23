package com.prezi.spaghetti.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
/**
 * Created by lptr on 19/11/13.
 */
class Parameters {
	private final Project project

	public Parameters(Project project) {
		this.project = project
	}

	String platform

	Configuration configuration

	void configuration(Configuration configuration) {
		this.configuration = configuration
	}

	Configuration obfuscatedConfiguration

	void obfuscatedConfiguration(Configuration obfuscatedConfiguration) {
		this.obfuscatedConfiguration = obfuscatedConfiguration
	}

	String sourceBaseUrl = "http://github.com/prezi/${project.rootProject.name}"

	void sourceBaseUrl(String source) {
		this.sourceBaseUrl = source
	}
}
