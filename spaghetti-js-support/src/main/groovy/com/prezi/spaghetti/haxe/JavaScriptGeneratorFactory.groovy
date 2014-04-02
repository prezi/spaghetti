package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration

/**
 * Created by lptr on 19/03/14.
 */
class JavaScriptGeneratorFactory implements GeneratorFactory {
	@Override
	String getPlatform() {
		return "js"
	}

	@Override
	String getDescription() {
		return "vanilla JavaScript support"
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration) {
		return new JavaScriptGenerator(configuration)
	}

	@Override
	Map<String, String> getExternMapping() {
		return [:]
	}
}
