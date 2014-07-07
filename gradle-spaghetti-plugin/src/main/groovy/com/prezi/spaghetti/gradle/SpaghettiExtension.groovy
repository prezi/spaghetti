package com.prezi.spaghetti.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class SpaghettiExtension {
	private final Project project

	String platform

	Configuration configuration

	void configuration(Configuration configuration) {
		this.configuration = configuration
	}

	Configuration obfuscatedConfiguration

	void obfuscatedConfiguration(Configuration obfuscatedConfiguration) {
		this.obfuscatedConfiguration = obfuscatedConfiguration
	}

	String sourceBaseUrl

	void sourceBaseUrl(String source) {
		this.sourceBaseUrl = source
	}

	SpaghettiExtension(Project project, Configuration defaultConfiguration, Configuration defaultObfuscatedConfiguration) {
		this.project = project
		this.configuration = defaultConfiguration
		this.obfuscatedConfiguration = defaultObfuscatedConfiguration
	}
}
