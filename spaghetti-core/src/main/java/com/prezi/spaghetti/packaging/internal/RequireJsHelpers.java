package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;

public class RequireJsHelpers {
	private static Collection<String> makeModuleDependencies(Collection<String> moduleNames, final String modulesRoot) {
		final String normalizedModulesRoot = modulesRoot.endsWith("/") ? modulesRoot : modulesRoot + "/";
		return Collections2.transform(moduleNames, new Function<String, String>() {
			@Override
			public String apply(String moduleName) {
				return String.format("\"%s\":\"%s%s/%s\"", moduleName, normalizedModulesRoot, moduleName, moduleName);
			}
		});
	}

	public static void makePathsMapping(StringBuilder result, String modulesDirectory, Collection<String> dependencies, Map<String, String> externals) {
		Iterable<String> moduleDependencyPaths = makeModuleDependencies(dependencies, modulesDirectory);
		Iterable<String> externalDependencyPaths = Collections2.transform(externals.entrySet(), new Function<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Map.Entry<String, String> external) {
				return String.format("\"%s\":\"%s\"", external.getKey(), external.getValue());
			}
		});

		result.append("{");
		Joiner.on(',').appendTo(result, Iterables.concat(moduleDependencyPaths, externalDependencyPaths));
		result.append("}");
	}

	public static void makeConfig(StringBuilder result, String modulesDirectory, Collection<String> dependencies, Map<String, String> externals) {
		// Begin config
		result.append("require[\"config\"]({\"baseUrl\":\".\",\"paths\":");
		// Append path definitions
		makePathsMapping(result, modulesDirectory, dependencies, externals);
		// End config
		result.append("});");
	}
}