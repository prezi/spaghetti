package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.ast.ModuleNode;

import java.io.File;
import java.io.IOException;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE_WRAPPER_FUNCTION;
import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS;

/**
 * Abstract implementation of {@link Generator}. Derive from this class instead of
 * implementing {@link Generator} directly for future compatibility.
 */
public abstract class AbstractGenerator implements Generator {

	@SuppressWarnings("UnusedParameters")
	public AbstractGenerator(GeneratorParameters params) {
	}

	@Override
	public final String processModuleJavaScript(ModuleNode module, String javaScript) {
		final String processedJavaScript = processModuleJavaScriptInternal(module, javaScript);
		return MODULE_WRAPPER_FUNCTION + "(function(" + SPAGHETTI_CLASS + ") {\n" + processedJavaScript + "\n})\n";
	}

	/**
	 * Method to wrap JavaScript inside the <code>module(function() { ... })</code> block.
	 *
	 * @param moduleDefinition the module to wrap.
	 * @param javaScript       the JavaScript to wrap.
	 * @return the wrapped JavaScript.
	 */
	protected abstract String processModuleJavaScriptInternal(ModuleNode moduleDefinition, String javaScript);

	@Override
	public void generateHeaders(File outputDirectory) throws IOException {
		// Do nothing by default
	}

	@Override
	public void generateStubs(File outputDirectory) throws IOException {
		// Do nothing by default
	}
}
