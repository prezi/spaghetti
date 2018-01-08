package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.generator.AbstractJavaScriptBundleProcessor
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters
import com.prezi.spaghetti.typescript.bundle.TypeScriptEnumDenormalizer
import java.util.Collection
import java.util.LinkedHashSet

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class TypeScriptJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {
	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"
	public static final String USE_STRICT = "\"use strict\";";

	TypeScriptJavaScriptBundleProcessor() {
		super("typescript")
	}

	@Override
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
		def module = params.moduleConfiguration.localModule

		def content = ""
		if (javaScript.startsWith(USE_STRICT)) {
			javaScript = javaScript.substring(USE_STRICT.length());
			content += USE_STRICT + "\n";
		}
		content += generateAccessors(params.moduleConfiguration)
		content += TypeScriptEnumDenormalizer.denormalize(javaScript)
		if (content.contains("var __spaghettiMainModule=")) {
			content += "\n" + "return __spaghettiMainModule;" + "\n"
		} else {
			def export = getModuleExport(module)
			content += "\n" + "return ${export};" + "\n"
		}
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

	private static String generateAccessors(ModuleConfiguration config) {
		// Use LinkedHashSet so the order of the lines is preserved,
		// but duplicate (redundant) lines are not generated for namespaces
		// which have a common prefix (ie. com.spaghetti.a, com.spaghetti.b).
		LinkedHashSet<String> lines = new LinkedHashSet<String>();
		for (def wrapper: config.getDirectDependentModules()) {
			ModuleNode module = wrapper.entity;
			String value = GeneratorUtils.createModuleAccessor(module.name, wrapper.format);
			Collection<String> namespaceMerge = GeneratorUtils.createNamespaceMerge(module.name, value);
			lines.addAll(namespaceMerge);
		}

		if (lines.isEmpty()) {
			return ""
		}
		return lines.join("\n") + "\n";
	}
}
