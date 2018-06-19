package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class CommonJsModuleWrapper extends AbstractModuleWrapper {
	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {

		Iterable<String> dependencies = Iterables.concat(
				params.externalDependencies.values(),
				Sets.newTreeSet(params.dependencies)
		);

		StringBuilder result = new StringBuilder();
		result.append(";(function(){");
		result.append("var baseUrl=__dirname;");
		result.append("module.exports=(");
		wrapModuleObject(
				result,
				params,
				true);
		result.append(")(");
		result.append(
				Stream.concat(
						Stream.concat(params.externalDependencies.values().stream(), Sets.newTreeSet(params.dependencies).stream()).map(dependency ->
								"require(\"" + dependency + "\")"
						),
						params.lazyDependencies.stream().map(lazyDependency ->
								"function(){return Promise.resolve(require(\"" + lazyDependency + "\").lazyModule);}"
						)
				).collect(Collectors.joining(","))
		);
		result.append(");");
		result.append("})();");

		return result.toString();
	}

	@Override
	protected StringBuilder makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("var mainModule=require(\"").append(mainModule).append("\")[\"").append(MODULE).append("\"];");
		if (execute) {
            result.append("mainModule[\"main\"]();\n");
        }
		return result;
	}

	@Override
	protected StringBuilder makeConfig(StringBuilder result, String modulesDirectory, Map<String, Set<String>> dependencyTree, Map<String, String> externals) {
		return result;
	}
}
