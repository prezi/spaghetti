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
			case Wrapping.nodeApp:
				return wrapAsNodeJsModule(config, contents)
		}
	}

	private static String wrapAsRequireJsModule(ModuleConfiguration config, String function, String contents) {
		def result = function
		result += "(["
		result += config.dependentModules.collect { module -> "\"${getFileName(module)}\"" }.join(",")
		result += "], function() {\n"
		result += "var __modules = arguments;\n"
		result += contents
		result += "\n});\n"
		return result
	}

	private static String wrapAsNodeJsModule(ModuleConfiguration config, String contents) {
		def result =
"""var __requirejs = require("requirejs");
var __modules = [];
"""
		config.dependentModules.each { module ->
			result +=
"""__modules.push(__requirejs(\"${getFileName(module)}\"));
"""
		}
		result += contents
		return result
	}

	static String getFileName(ModuleDefinition module)
	{
		return module.name.localName
	}
}
