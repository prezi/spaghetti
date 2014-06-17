package com.prezi.spaghetti;

import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.config.ModuleConfiguration;

import static com.prezi.spaghetti.ReservedWords.CONFIG;
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION;

public abstract class AbstractGenerator implements Generator {

	protected final ModuleConfiguration config;

	public AbstractGenerator(ModuleConfiguration config) {
		this.config = config;
	}

	@Override
	public String processApplicationJavaScript(String javaScript) {
		return javaScript;
	}

	@Override
	public final String processModuleJavaScript(ModuleNode module, String javaScript) {
		final String processedJavaScript = processModuleJavaScriptInternal(module, javaScript);
		return SPAGHETTI_WRAPPER_FUNCTION + "(function(" + CONFIG + ") {\n" + processedJavaScript + "\n});\n";
	}

	protected abstract String processModuleJavaScriptInternal(ModuleNode moduleDefinition, String javaScript);

}
