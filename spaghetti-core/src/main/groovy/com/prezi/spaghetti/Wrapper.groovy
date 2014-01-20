package com.prezi.spaghetti
/**
 * Created by lptr on 11/12/13.
 */
class Wrapper {
	public static String wrap(ModuleConfiguration config, Wrapping wrapping, String contents) {
		switch (wrapping) {
			case Wrapping.application:
				return wrapAsRequireJsModule(config, "require", contents)
			case Wrapping.module:
				return wrapAsRequireJsModule(config, "define", contents)
			case Wrapping.nodeModule:
				return wrapAsNodeJsModule(config, contents)
		}
	}

	private static String wrapAsRequireJsModule(ModuleConfiguration config, String function, String contents) {
		def fileNames = config.dependentModules.collect { module ->
			return "\"${getFileName(module)}\""
		}
		def modules = []
		config.dependentModules.eachWithIndex { module, index ->
			modules.push(""""${module.name.fullyQualifiedName}": arguments[${index}]""")
		}
		return """/* Generated by Spaghetti */ ${function}([${fileNames.join(",")}], function() { var __modules = { ${modules.join(",")} }; ${contents}
});
"""
	}

	private static String wrapAsNodeJsModule(ModuleConfiguration config, String contents) {
		def requires = config.dependentModules.collect { module ->
			""""${module.name.fullyQualifiedName}": __requirejs("${getFileName(module)}")"""
		}
		return """/* Generated by Spaghetti */ var __requirejs = require("requirejs"); var __modules = { ${requires.join("")} }; ${contents}"""
	}

	static String getFileName(ModuleDefinition module)
	{
		return module.name.fullyQualifiedName
	}
}
