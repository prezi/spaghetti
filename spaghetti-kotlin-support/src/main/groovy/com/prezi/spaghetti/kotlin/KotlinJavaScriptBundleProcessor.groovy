package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.AbstractJavaScriptBundleProcessor
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters

class KotlinJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {
	public static final String KOTLIN_MODULE_VAR = "__kotlinModule"

	KotlinJavaScriptBundleProcessor() {
		super("kotlin")
	}

	@Override
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
"""
var ${KOTLIN_MODULE_VAR};
${javaScript}
return ${KOTLIN_MODULE_VAR};
"""
	}

	@Override
	Set<String> getProtectedSymbols() {
		return []
	}
}
