package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.bundle.ModuleFormat;

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
	public static String createModuleAccessor(String moduleName, ModuleFormat format) {
		if (format == ModuleFormat.Wrapperless) {
			return String.format("%s[\"%s\"][\"%s\"][\"%s\"]",
				ReservedWords.SPAGHETTI_CLASS,
				ReservedWords.DEPENDENCIES,
				moduleName,
				ReservedWords.MODULE);
		} else {
			return String.format("%s[\"%s\"][\"%s\"]",
					ReservedWords.SPAGHETTI_CLASS,
					ReservedWords.DEPENDENCIES,
					moduleName);
		}
	}
}
