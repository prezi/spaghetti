package com.prezi.spaghetti
/**
 * Created by lptr on 11/12/13.
 */
class Wrapper {
	public static String wrap(Iterable<String> dependentModuleNames, Wrapping wrapping, String contents) {
		def wrapped
		switch (wrapping) {
			case Wrapping.application:
				wrapped = wrapAsRequireJsModule(dependentModuleNames, "require", contents)
				break
			case Wrapping.module:
				wrapped = wrapAsRequireJsModule(dependentModuleNames, "define", contents)
				break
			case Wrapping.nodeModule:
				wrapped = wrapAsNodeJsModule(dependentModuleNames, "", contents)
				break
			default:
				throw new IllegalArgumentException("Unknown wrapping: ${wrapping}")
		}
		return "/* Generated by Spaghetti */ " + wrapped
	}

	public static String wrapWithConfig(Iterable<String> dependentModuleNames, Wrapping wrapping, String modulesRoot, String contents) {
		String configured
		switch (wrapping) {
			case Wrapping.application:
				configured = config(dependentModuleNames, modulesRoot, "require") + wrapAsRequireJsModule(dependentModuleNames, "require", contents)
				break
			case Wrapping.nodeModule:
				configured = wrapAsNodeJsModule(dependentModuleNames, config(dependentModuleNames, modulesRoot, "__requirejs"), contents)
				break
			case Wrapping.module:
				throw new IllegalArgumentException("Cannot generate configuration for wrapping type module.")
			default:
				throw new IllegalArgumentException("Unknown wrapping: ${wrapping}")
		}
		return "/* Generated by Spaghetti */ " + configured
	}

	private static String wrapAsRequireJsModule(Iterable<String> moduleNames, String function, String contents) {
		def fileNames = moduleNames.collect { moduleName ->
			return "\"${moduleName}\""
		}
		def modules = []
		moduleNames.eachWithIndex { moduleName, index ->
			modules.push(""""${moduleName}": arguments[${index}]""")
		}
		return """${function}([${fileNames.join(",")}], function() { var __modules = { ${modules.join(",")} }; ${contents}
});
"""
	}

	private static String wrapAsNodeJsModule(Iterable<String> moduleNames, String config, String contents) {
		def requires = moduleNames.collect { moduleName ->
			""""${moduleName}": __requirejs("${moduleName}")"""
		}
		return """var __requirejs = require("requirejs"); ${config} var __modules = { ${requires.join(",")} }; exports = exports || {}; ${contents}"""
	}

	private static String config(Iterable<String> moduleNames, String modulesRoot, String requireName) {
		if (modulesRoot && !modulesRoot.endsWith("/")) {
			modulesRoot += "/"
		}
		return requireName + ".config({" +
				"paths: {" +
				moduleNames.collect { moduleName ->
					"\"${moduleName}\": \"${modulesRoot}${moduleName}/${moduleName}\""
				}.join(",") + "}});"
	}
}
