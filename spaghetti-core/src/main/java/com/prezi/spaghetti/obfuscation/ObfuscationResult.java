package com.prezi.spaghetti.obfuscation;

public class ObfuscationResult {
	public final String javaScript;
	public final String sourceMap;

	public ObfuscationResult(String javaScript, String sourceMap) {
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
	}
}
