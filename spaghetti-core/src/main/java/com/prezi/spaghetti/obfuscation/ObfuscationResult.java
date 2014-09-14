package com.prezi.spaghetti.obfuscation;

/**
 * The results of the obfuscation of a module's JavaScript.
 */
public class ObfuscationResult {
	/**
	 * The obfuscated JavaScript.
	 */
	public final String javaScript;

	/**
	 * The updated source map of the module.
	 */
	public final String sourceMap;

	public ObfuscationResult(String javaScript, String sourceMap) {
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
	}
}
