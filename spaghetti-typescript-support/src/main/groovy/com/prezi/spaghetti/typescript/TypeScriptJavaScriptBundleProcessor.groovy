package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.generator.AbstractJavaScriptBundleProcessor
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters
import com.prezi.spaghetti.typescript.bundle.TypeScriptEnumDenormalizer

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class TypeScriptJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {
	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"

	TypeScriptJavaScriptBundleProcessor() {
		super("typescript")
	}

	@Override
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
		def content = TypeScriptEnumDenormalizer.denormalize(javaScript);
		def module = params.moduleConfiguration.localModule
		if (module.source.definitionLanguage == DefinitionLanguage.TypeScript) {
			content += "\n"
			content += "return ${module.name};"
			content += "\n"
		} else {
			content += "\n"
			content += "return ${module.name}.${CREATE_MODULE_FUNCTION}(${SPAGHETTI_CLASS});"
			content += "\n"
		}

		return content
	}

	@Override
	Set<String> getProtectedSymbols() {
		return [].asImmutable()
	}
}
