package com.prezi.spaghetti.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lptr on 15/02/14.
 */
class SpaghettiBasePlugin implements Plugin<Project> {
	static final String CONFIGURATION_NAME = "modules"
	static final String OBFUSCATED_CONFIGURATION_NAME = "modulesObf"

	@Override
	void apply(Project project) {
		def defaultConfiguration = project.configurations.findByName(CONFIGURATION_NAME)
		if (defaultConfiguration == null) {
			defaultConfiguration = project.configurations.create(CONFIGURATION_NAME)
		}

		def defaultObfuscatedConfiguration = project.configurations.findByName(OBFUSCATED_CONFIGURATION_NAME)
		if (defaultObfuscatedConfiguration == null) {
			defaultObfuscatedConfiguration = project.configurations.create(OBFUSCATED_CONFIGURATION_NAME);
		}

		project.extensions.create "spaghetti", SpaghettiExtension, project, defaultConfiguration, defaultObfuscatedConfiguration
	}
}
