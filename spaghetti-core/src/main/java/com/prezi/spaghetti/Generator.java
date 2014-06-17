package com.prezi.spaghetti;

import com.prezi.spaghetti.ast.ModuleNode;

import java.io.File;

public interface Generator {
	/**
	 * Generate headers.
	 *
	 * @param outputDirectory the directory to generate header files into.
	 */
	void generateHeaders(File outputDirectory);

	/**
	 * Process the JavaScript code of a module.
	 * Needs to provide a function call to <code>spaghetti()</code> that returns a module object.
	 * <p/>
	 * <pre>
	 *     spaghetti(function(SpaghettiConfiguration) {
	 *         var modules = SpaghettiConfiguration["__modules"];
	 *         var baseUrl = SpaghettiConfiguration["__baseUrl"];
	 *         // Do stuff
	 *         return {
	 *             "__instance": { ... }, // the module object instance
	 *             "__static": { ... } // exposed static methods
	 *         }
	 *     });
	 * </pre>
	 *
	 * @param module     the module the JavaScript is being processed for.
	 * @param javaScript the actual JavaScript.
	 * @return the processed JavaScript for the module.
	 */
	String processModuleJavaScript(ModuleNode module, String javaScript);

	/**
	 * Process the JavaScript code of an application before wrapping it into a Require JS wrapper.
	 *
	 * @param javaScript the actual JavaScript.
	 * @return the processed JavaScript for the application.
	 */
	String processApplicationJavaScript(String javaScript);
}
