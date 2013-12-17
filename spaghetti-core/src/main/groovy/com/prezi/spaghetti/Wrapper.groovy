package com.prezi.spaghetti
/**
 * Created by lptr on 11/12/13.
 */
class Wrapper {
	public static String wrap(ModuleConfiguration config, Wrapping wrapping, String contents) {
		def result
		switch (wrapping) {
			case Wrapping.application:
				result = "require"
				break;
			case Wrapping.module:
				result = "define"
				break;
		}
		result += "(["
		result += config.dependentModules.collect { "\"${it.name.localName}\"" }.join(",")
		result += "], function() {\n"
		result += "var __modules = arguments;\n"
		result += contents
		result += "\n});\n"
		return result
	}
}
