package com.prezi.gradle.spaghetti

import org.gradle.api.Project

/**
 * Created by lptr on 12/11/13.
 */
public interface Generator {
	void initialize(Project project)
	String getPlatform();
	void generateInterfaces(ModuleDefinition moduleDefinition, File outputDirectory);
	void generateClientModule(ModuleDefinition moduleDefinition, File outputDirectory);
}
