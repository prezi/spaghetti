package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.generator.AbstractJavaScriptBundleProcessor
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters

class HaxeJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {

	// Workaround variable to trick Haxe into exposing the module
	public static final String HAXE_MODULE_VAR = "__haxeModule"

	HaxeJavaScriptBundleProcessor() {
		super("haxe")
	}

	@Override
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
"""// Haxe expects either window or exports to be present
var exports = exports || {};
var ${HAXE_MODULE_VAR};
${javaScript}
return ${HAXE_MODULE_VAR};
"""
	}

	@Override
	Set<String> getProtectedSymbols() {
		return [
				// Haxe likes to put this on global objects like Math and String and Date
				"__name__"
		]
	}
}
