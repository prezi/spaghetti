package com.prezi.spaghetti.generator;

import java.util.ArrayList;
import java.util.List;

import com.prezi.spaghetti.bundle.ModuleFormat;

/**
 * Provide common functions for target language generators.
 */
public class GeneratorUtils {
	/**
	 * Generate JavaScript code for accessing the exported module of a dependency.
	 *
	 * @param moduleName The name of the module dependency to create an accessor for
	 * @return JavaScript expression to access foreign module
	 */
	public static String createModuleAccessor(String moduleName, ModuleFormat format) {
		if (format == ModuleFormat.Wrapperless) {
			return String.format("%s[\"%s\"][\"%s\"][\"%s\"]",
				ReservedWords.SPAGHETTI_CLASS,
				ReservedWords.DEPENDENCIES,
				moduleName,
				ReservedWords.MODULE);
		} else {
			return String.format("%s[\"%s\"][\"%s\"]",
					ReservedWords.SPAGHETTI_CLASS,
					ReservedWords.DEPENDENCIES,
					moduleName);
		}
	}

	public static List<String> createNamespaceMerge(String namespace, String value) {
		List<String> lines = new ArrayList<String>();
		if (namespace.contains(".")) {
			String[] split = namespace.split("\\.");
			String path = split[0];

			lines.add(String.format("var %s=(%s||{});", path, path));
			for (int i = 1; i < split.length - 1; i++) {
				path += "." + split[i];
				lines.add(String.format("%s=(%s||{});", path, path));
			}
			lines.add(String.format("%s=%s;", namespace, value));
		} else {
			lines.add(String.format("var %s=%s;", namespace, value));
		}
		return lines;
	}

	public static String namespaceToIdentifier(String namespace) {
		return namespace.replaceAll("^[^a-zA-Z_$]|[^\\w$]", "_");
	}

	public static String createLazyModuleAccessorName(String moduleName) {
		return "get_" + namespaceToIdentifier(moduleName);
	}
}
