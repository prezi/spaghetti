package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.ast.ModuleNode;

/**
 * Provide common functions for target language generators.
 */
public class GeneratorUtils {
	/**
	 * Generate JavaScript code for accessing the exported module of a dependency.
	 *
	 * @param moduleName The name of the module dependency to create an accessor for
	 * @return JavaScript expression to access foreign module
	 */
	public static String createModuleAccessor(String moduleName) {
		return String.format("%s[\"%s\"][\"%s\"][\"%s\"]",
				ReservedWords.SPAGHETTI_CLASS,
				ReservedWords.DEPENDENCIES,
				moduleName,
				ReservedWords.MODULE);
	}
}
