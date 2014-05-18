package com.prezi.spaghetti

import com.prezi.spaghetti.definition.ModuleConfiguration
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
	final String processModuleJavaScript(ModuleDefinition module, ModuleConfiguration config, String javaScript) {
		def processedJavaScript = processModuleJavaScriptInternal(module, config, javaScript)
		return \
"""${SPAGHETTI_WRAPPER_FUNCTION}(function(${CONFIG}) {
${processedJavaScript}
});
"""
	}

	abstract protected String processModuleJavaScriptInternal(ModuleDefinition moduleDefinition, ModuleConfiguration config, String javaScript)
}
