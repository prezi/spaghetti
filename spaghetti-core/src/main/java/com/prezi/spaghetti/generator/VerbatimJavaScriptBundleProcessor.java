package com.prezi.spaghetti.generator;

import java.util.Collections;
import java.util.Set;

/**
 * A JavaScript bundle processor that returns the given JavaScript without modifications.
 */
public class VerbatimJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {
	public VerbatimJavaScriptBundleProcessor(String language) {
		super(language);
	}

	@Override
	public String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
		return javaScript;
	}

	@Override
	public Set<String> getProtectedSymbols() {
		return Collections.emptySet();
	}
}
