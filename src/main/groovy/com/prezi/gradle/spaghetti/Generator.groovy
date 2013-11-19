package com.prezi.gradle.spaghetti

/**
 * Created by lptr on 12/11/13.
 */
abstract public class Generator {
	protected final String platform

	protected Generator(String platform) {
		this.platform = platform
	}

	abstract void generateInterfaces(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	abstract void generateClientModule(ModuleConfiguration config, File outputDirectory)
	abstract String processModuleJavaScript(ModuleConfiguration config, ModuleDefinition module, String javaScript)
}
