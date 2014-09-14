package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.ast.ModuleNode;

import java.io.File;
import java.io.IOException;

/**
 * Generates sources for a language.
 */
public interface Generator {
	/**
	 * Generate headers.
	 *
	 * @param outputDirectory the directory to generate header files into.
	 */
	void generateHeaders(File outputDirectory) throws IOException;

	/**
	 * Generate stub implementation.
	 *
	 * @param outputDirectory the directory to generate header files into.
	 */
	void generateStubs(File outputDirectory) throws IOException;

	/**
	 * Process the JavaScript code of a module.
	 * Needs to provide a function call to <code>spaghetti()</code> that returns a module object.
	 *
	 * <pre>
	 *     module(function(Spaghetti) {
	 *         // original code
	 *         return module;
	 *     })
	 * </pre>
	 *
	 * @param module     the module the JavaScript is being processed for.
	 * @param javaScript the actual JavaScript.
	 * @return the processed JavaScript for the module.
	 */
	String processModuleJavaScript(ModuleNode module, String javaScript);
}
