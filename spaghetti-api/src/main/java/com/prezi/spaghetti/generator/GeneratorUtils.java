package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.ast.ModuleNode;

/**
 * Provide common functions for target language generators.
 */
public class GeneratorUtils {
	/**
	 * Create JavaScript code for accessing the exported module of a dependency.
	 *
	 * @param node The module dependency to create an accessor for
	 * @return JS expression to access foreign module
	 */
	public static String createModuleAccessor(ModuleNode node) {
		return String.format("%s[\"%s\"][\"%s\"][\"%s\"]",
				ReservedWords.SPAGHETTI_CLASS,
				ReservedWords.DEPENDENCIES,
				node.getName(),
				ReservedWords.MODULE);
	}
}
