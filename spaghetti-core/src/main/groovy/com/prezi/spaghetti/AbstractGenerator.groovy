package com.prezi.spaghetti

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.config.ModuleConfiguration

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

/**
 * Created by lptr on 16/05/14.
 */
abstract class AbstractGenerator implements Generator {
	protected final ModuleConfiguration config

	AbstractGenerator(ModuleConfiguration config) {
		this.config = config
	}

	@Override
	String processApplicationJavaScript(String javaScript) {
		return javaScript
	}

	@Override
	final String processModuleJavaScript(ModuleNode module, String javaScript) {
		def processedJavaScript = processModuleJavaScriptInternal(module, javaScript)
		return \
"""${SPAGHETTI_WRAPPER_FUNCTION}(function(${CONFIG}) {
${processedJavaScript}
});
"""
	}

	abstract protected String processModuleJavaScriptInternal(ModuleNode moduleDefinition, String javaScript)
}
