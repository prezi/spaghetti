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
import java.util.stream.Collectors;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class AmdModuleWrapper extends AbstractModuleWrapper {

	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		StringBuilder result = new StringBuilder();

		result
				.append("define(")
				.append("\"" + params.name + "\",")
				.append("[\"")
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
				true);
		result.append(").apply({},[].slice.call(arguments,1)");
		if (params.lazyDependencies.size() > 0) {
			result.append(".concat([");
			result.append(
					params.lazyDependencies.stream().map(lazyDependency ->
							"function(){return new Promise(function(resolve,reject){require([\"" + lazyDependency + "\"],function(m){resolve(m.lazyModule);},function(err){reject(err);});});}"
					).collect(Collectors.joining(","))
			);
			result.append("])");
		}
		result.append(");");
		result.append("});");

		return result.toString();
	}

	@Override
	protected void makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("require([\"").append(mainModule).append("\"],function(__mainModule){");
		if (execute) {
            result.append("__mainModule[\"").append(MODULE).append("\"][\"main\"]();");
        }
		result.append("});\n");
	}

	@Override
	protected void makeConfig(StringBuilder result, String modulesDirectory, Collection<String> dependencies, Map<String, String> externals) {
		RequireJsHelpers.makeConfig(result, modulesDirectory, dependencies, externals);
	}

	@Override
	public String makeJsonPathsMapping(Collection<String> dependencies, String modulesDirectory, Map<String, String> externals) {
		StringBuilder result = new StringBuilder();
		RequireJsHelpers.makePathsMapping(result, modulesDirectory, dependencies, externals);
		result.append("\n");
		return result.toString();
	}
}
