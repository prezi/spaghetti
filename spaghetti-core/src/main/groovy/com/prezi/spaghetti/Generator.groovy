package com.prezi.spaghetti

import com.prezi.spaghetti.definition.ModuleDefinition

/**
 * Created by lptr on 12/11/13.
 */
public interface Generator {
	abstract void generateHeaders(File outputDirectory)

	/**
	 * Process the JavaScript code of a module before wrapping it into a Require JS wrapper.
	 */
	String processModuleJavaScript(ModuleDefinition module, String javaScript)

	/**
	 * Process the JavaScript code of an application before wrapping it into a Require JS wrapper.
	 */
	String processApplicationJavaScript(String javaScript);
}
