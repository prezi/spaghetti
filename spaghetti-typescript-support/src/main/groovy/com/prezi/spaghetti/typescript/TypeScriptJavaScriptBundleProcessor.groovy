package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode
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
		def module = params.moduleConfiguration.localModule
		def export = getModuleExport(module)

		def content = ""
		content += TypeScriptEnumDenormalizer.denormalize(javaScript)
		content += "\n" + "return ${export};" + "\n"
		return content
	}

	@Override
	Set<String> getProtectedSymbols() {
		return [].asImmutable()
	}

	private static String getModuleExport(ModuleNode module) {
		if (module.source.definitionLanguage == DefinitionLanguage.TypeScript) {
			return module.name;
		} else {
			return "${module.name}.${CREATE_MODULE_FUNCTION}(${SPAGHETTI_CLASS})"
		}
	}
}
