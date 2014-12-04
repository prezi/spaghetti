package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.AbstractJavaScriptBundleProcessor
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class TypeScriptJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {
	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"

	TypeScriptJavaScriptBundleProcessor() {
		super("typescript")
	}

	@Override
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
"""${javaScript}
return ${params.moduleConfiguration.localModule.name}.${CREATE_MODULE_FUNCTION}(${SPAGHETTI_CLASS});
"""
	}

	@Override
	Set<String> getProtectedSymbols() {
		return [].asImmutable()
	}
}
