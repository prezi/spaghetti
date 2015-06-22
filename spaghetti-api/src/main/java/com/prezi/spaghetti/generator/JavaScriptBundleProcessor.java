package com.prezi.spaghetti.generator;

import java.util.Set;

/**
 * Processes the JavaScript output of a language's compiler in order to include
 * the resulting JavaScript in a Spaghetti bundle.
 */
public interface JavaScriptBundleProcessor extends GeneratorService {
	/**
	 * Returns the supported language of the generator.
	 * @return the supported language of the generator.
	 */
	@Override
	String getLanguage();

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
	 * @param params the parameters for processing.
	 * @param javaScript the actual JavaScript.
	 * @return the processed JavaScript for the module.
	 */
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript);

	/**
	 * Returns a set of symbols that need to be protected by the obfuscator.
	 *
	 * @return a set of symbols.
	 */
	Set<String> getProtectedSymbols();
}
