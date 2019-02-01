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
import java.util.regex.Pattern

class TypeScriptJavaScriptBundleProcessor extends AbstractJavaScriptBundleProcessor {
	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"
	public static final Pattern USE_STRICT = Pattern.compile("^['\"]use strict['\"];");

	TypeScriptJavaScriptBundleProcessor() {
		super("typescript")
	}

	@Override
	String processModuleJavaScript(JavaScriptBundleProcessorParameters params, String javaScript) {
		def module = params.moduleConfiguration.localModule

		def content = ""
		def matcher = USE_STRICT.matcher(javaScript)
		if (matcher.find()) {
			javaScript = javaScript.substring(matcher.end());
			content += "'use strict';\n";
		}

		content += generateAccessors(params.moduleConfiguration)
		content += TypeScriptEnumDenormalizer.denormalize(javaScript)
		def export = getModuleExport(module)
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
			return "${CREATE_MODULE_FUNCTION}()"
		}
	}

	private static String generateAccessors(ModuleConfiguration config) {
		// Use LinkedHashSet so the order of the lines is preserved,
		// but duplicate (redundant) lines are not generated for namespaces
		// which have a common prefix (ie. com.spaghetti.a, com.spaghetti.b).
		LinkedHashSet<String> lines = new LinkedHashSet<String>();

		// Create local variable for current module's namespace and initialize it to null.
		// To prevent module's code from accidentally assigning to a global variable.
		lines.addAll(GeneratorUtils.createNamespaceMerge(config.localModule.name, "null"))

		for (def wrapper: config.getDirectDependentModules()) {
			ModuleNode module = wrapper.entity;
			String value = GeneratorUtils.createModuleAccessor(module.name, wrapper.format);
			lines.add(String.format("var %s=%s;", GeneratorUtils.namespaceToIdentifier(module.name), value));
		}
		for (def wrapper: config.getLazyDependentModules()) {
			ModuleNode module = wrapper.entity;
			String value = GeneratorUtils.createModuleAccessor(module.name, wrapper.format);
			lines.add(String.format("var %s=%s;", GeneratorUtils.createLazyModuleAccessorName(module.name), value));
		}

		if (lines.isEmpty()) {
			return ""
		}
		return lines.join("\n") + "\n";
	}
}
