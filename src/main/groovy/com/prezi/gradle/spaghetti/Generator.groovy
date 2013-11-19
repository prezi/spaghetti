package com.prezi.gradle.spaghetti

/**
 * Created by lptr on 12/11/13.
 */
abstract public class Generator {
	protected final String platform

	protected Generator(String platform) {
		this.platform = platform
	}

	abstract void generateModuleHeaders(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	abstract void generateApplication(ModuleConfiguration config, File outputDirectory)

	/**
	 * Process the JavaScript code of a module before wrapping it into a Require JS wrapper.
	 */
	@SuppressWarnings("GrMethodMayBeStatic")
	String processModuleJavaScript(ModuleConfiguration config, ModuleDefinition module, String javaScript) {
		return javaScript
	}

	/**
	 * Process the JavaScript code of an application before wrapping it into a Require JS wrapper.
	 */
	@SuppressWarnings("GrMethodMayBeStatic")
	String processApplicationJavaScript(ModuleConfiguration config, String javaScript) {
		return javaScript
	}
}
