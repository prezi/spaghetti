package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGeneratorFactory
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration

/**
 * Created by lptr on 19/03/14.
 */
class JavaScriptGeneratorFactory extends AbstractGeneratorFactory {

	JavaScriptGeneratorFactory() {
		super("js", "vanilla JavaScript support")
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration) {
		return new JavaScriptGenerator(configuration)
	}
}
