package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition

/**
 * Created by lptr on 19/03/14.
 */
class JavaScriptGenerator implements Generator {

	private final ModuleConfiguration config

	JavaScriptGenerator(ModuleConfiguration config) {
		this.config = config
	}

	@Override
	void generateHeaders(File outputDirectory) {
	}

	@Override
	String processModuleJavaScript(ModuleDefinition module, String javaScript) {
		return javaScript
	}

	@Override
	String processApplicationJavaScript(String javaScript) {
		return javaScript
	}
}
