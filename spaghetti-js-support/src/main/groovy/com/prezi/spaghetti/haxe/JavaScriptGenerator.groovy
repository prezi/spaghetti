package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.config.ModuleConfiguration

/**
 * Created by lptr on 19/03/14.
 */
class JavaScriptGenerator extends AbstractGenerator {

	JavaScriptGenerator(ModuleConfiguration config) {
		super(config)
	}

	@Override
	void generateHeaders(File outputDirectory) {
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript) {
		return javaScript
	}
}
