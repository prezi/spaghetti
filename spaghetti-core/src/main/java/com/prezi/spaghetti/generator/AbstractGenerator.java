package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.ast.ModuleNode;

import java.io.File;
import java.io.IOException;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE_WRAPPER_FUNCTION;
import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS;

public abstract class AbstractGenerator implements Generator {

	@SuppressWarnings("UnusedParameters")
	public AbstractGenerator(GeneratorParameters params) {
	}

	@Override
	public String processApplicationJavaScript(String javaScript) {
		return javaScript;
	}

	@Override
	public final String processModuleJavaScript(ModuleNode module, String javaScript) {
		final String processedJavaScript = processModuleJavaScriptInternal(module, javaScript);
		return MODULE_WRAPPER_FUNCTION + "(function(" + SPAGHETTI_CLASS + ") {\n" + processedJavaScript + "\n})\n";
	}

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
