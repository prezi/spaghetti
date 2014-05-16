package com.prezi.spaghetti

import com.prezi.spaghetti.definition.ModuleDefinition

/**
 * Created by lptr on 12/11/13.
 */
public interface Generator {
	public static final String CONFIG = "__config"

	abstract void generateHeaders(File outputDirectory)

	/**
	 * Process the JavaScript code of a module.
	 * Needs to provide a function call to <code>spaghetti()</code> that returns a module object.
	 *
	 * <pre>
	 *     spaghetti(function(__config) {
	 *         var modules = __config.__modules;
	 *         var baseUrl = __config.__baseUrl;
	 *         // Do stuff
	 *         return {
	 *             __module: ..., // the module object
	 *             __constants: ... // constants
	 *         }
	 *     });
	 * </pre>
	 */
	String processModuleJavaScript(ModuleDefinition module, String javaScript)

	/**
	 * Process the JavaScript code of an application before wrapping it into a Require JS wrapper.
	 */
	String processApplicationJavaScript(String javaScript);
}
