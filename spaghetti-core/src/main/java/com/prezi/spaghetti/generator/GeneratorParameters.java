package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.definition.ModuleConfiguration;

/**
 * Parameters for source code generation.
 */
public interface GeneratorParameters extends ParametersBase {
	/**
	 * The module configuration to generate sources for.
	 */
	ModuleConfiguration getModuleConfiguration();

	/**
	 * The header to be put in front of each generated source file.
	 */
	String getHeader();
}
