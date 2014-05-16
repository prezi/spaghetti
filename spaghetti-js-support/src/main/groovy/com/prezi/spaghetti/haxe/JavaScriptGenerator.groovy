package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition

/**
 * Created by lptr on 19/03/14.
 */
class JavaScriptGenerator extends AbstractGenerator {

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
}
