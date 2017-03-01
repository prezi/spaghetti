package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class AmdModuleWrapper extends AbstractModuleWrapper {

	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		StringBuilder result = new StringBuilder();

		result
				.append("define([\"")
				.append(Joiner.on("\",\"").join(Iterables.concat(
						Arrays.asList("require"),
						params.externalDependencies.values(),
						Sets.newTreeSet(params.dependencies)))
				)
				.append("\"],function(){");

		result.append("var moduleUrl=arguments[0][\"toUrl\"](\"" + params.name + ".js\");");
		result.append("var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\"));");
		result.append("return");
		result.append("(");
		wrapModuleObject(
				result,
				params,
				params.dependencies,
				params.externalDependencies.keySet(),
				true);
		result.append(").apply({},[].slice.call(arguments,1));");
		result.append("});");

		return result.toString();
	}

	@Override
	protected StringBuilder makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("require([\"").append(mainModule).append("\"],function(__mainModule){");
		if (execute) {
            result.append("__mainModule[\"").append(MODULE).append("\"][\"main\"]();");
        }
		result.append("});\n");
		return result;
	}

	@Override
	protected StringBuilder makeConfig(StringBuilder result, String modulesDirectory, Map<String, Set<String>> dependencyTree, Map<String, String> externals) {
		Iterable<String> moduleDependencyPaths = makeModuleDependencies(Sets.newTreeSet(dependencyTree.keySet()), modulesDirectory);
		Iterable<String> externalDependencyPaths = Collections2.transform(externals.entrySet(), new Function<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Map.Entry<String, String> external) {
				return String.format("\"%s\":\"%s\"", external.getKey(), external.getValue());
			}
		});
		// Begin config
		result.append("require[\"config\"]({\"baseUrl\":\".\",\"paths\":{");
		// Append path definitions
		Joiner.on(',').appendTo(result, Iterables.concat(moduleDependencyPaths, externalDependencyPaths));
		// End config
		result.append("}});");
		return result;
	}

	private static Collection<String> makeModuleDependencies(Collection<String> moduleNames, final String modulesRoot) {
		final String normalizedModulesRoot = modulesRoot.endsWith("/") ? modulesRoot : modulesRoot + "/";
		return Collections2.transform(moduleNames, new Function<String, String>() {
			@Override
			public String apply(String moduleName) {
				return String.format("\"%s\":\"%s%s/%s\"", moduleName, normalizedModulesRoot, moduleName, moduleName);
			}
		});
	}

}
