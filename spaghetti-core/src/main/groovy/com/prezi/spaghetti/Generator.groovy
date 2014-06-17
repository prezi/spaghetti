package com.prezi.spaghetti

import com.prezi.spaghetti.ast.ModuleNode

public interface Generator {

	abstract void generateHeaders(File outputDirectory)

	/**
	 * Process the JavaScript code of a module.
	 * Needs to provide a function call to <code>spaghetti()</code> that returns a module object.
	 *
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
	 */
	String processModuleJavaScript(ModuleNode module, String javaScript)

	/**
	 * Process the JavaScript code of an application before wrapping it into a Require JS wrapper.
	 */
	String processApplicationJavaScript(String javaScript)
}
