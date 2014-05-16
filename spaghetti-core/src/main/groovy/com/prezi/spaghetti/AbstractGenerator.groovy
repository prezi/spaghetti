package com.prezi.spaghetti

import com.prezi.spaghetti.definition.ModuleDefinition

import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

/**
 * Created by lptr on 16/05/14.
 */
abstract class AbstractGenerator implements Generator {
	@Override
	String processApplicationJavaScript(String javaScript) {
		return javaScript
	}

	@Override
	final String processModuleJavaScript(ModuleDefinition module, String javaScript) {
		def processedJavaScript = processModuleJavaScriptInternal(module, javaScript)
		return \
"""${SPAGHETTI_WRAPPER_FUNCTION}(function(${CONFIG}) {
${processedJavaScript}
});
"""
	}

	abstract protected String processModuleJavaScriptInternal(ModuleDefinition moduleDefinition, String javaScript)
}
