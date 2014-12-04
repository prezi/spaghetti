package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.definition.ModuleConfiguration;

/**
 * Parameters for the JavaScript processor.
 */
public interface JavaScriptBundleProcessorParameters {
	/**
	 * The module configuration to generate sources for.
	 */
	ModuleConfiguration getModuleConfiguration();
}
