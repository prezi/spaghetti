package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class UmdModuleWrapper extends AbstractModuleWrapper {

	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		StringBuilder result = new StringBuilder();

		result.append(";(function(){");
		result.append("var baseUrl;");
		result.append("var __factory=");
		wrapModuleObject(
				result,
				params,
				params.dependencies,
				params.externalDependencies.keySet(),
				false);
		result.append(";");

		// AMD
		result.append("if(typeof define===\"function\"&&define.amd){");
		String baseUrlDeclaration = "var moduleUrl=arguments[0][\"toUrl\"](\"" + params.name + ".js\");"
			+ "baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\"));";
		result
				.append("define([\"")
				.append(Joiner.on("\",\"").join(Iterables.concat(
						Arrays.asList("require"),
						params.externalDependencies.values(),
						Sets.newTreeSet(params.dependencies)))
				)
				.append("\"],function(){");
		result.append(baseUrlDeclaration);
		result.append("return");
		result.append("(__factory).apply({},[].slice.call(arguments,1));");
		result.append("});");

		// CommonJS
		Iterable<String> dependencies = Iterables.concat(
				params.externalDependencies.values(),
				Sets.newTreeSet(params.dependencies)
		);

		result.append("}else if(typeof exports===\"object\"&&typeof exports.nodeName!==\"string\"){");
		result.append("baseUrl=__dirname;");
		result.append("module.exports=(__factory)");
		result
				.append("(")
				.append(Joiner.on(",").join(Iterables.transform(dependencies, new Function<String, String>() {
					@Nullable
					@Override
					public String apply(String name) {
						return "require(\"" + name + "\")";
					}
				})))
				.append(");");
		//result.append("return module.exports;");
		result.append("}");

		result.append("})();");

		return result.toString();
	}

	@Override
	protected StringBuilder makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("if(typeof define===\"function\"&&define.amd){");
		result.append("require([\"").append(mainModule).append("\"],function(__mainModule){");
		if (execute) {
			result.append("__mainModule[\"main\"]();");
		}
		result.append("});");
		result.append("}else if(typeof exports===\"object\"&&typeof exports.nodeName!==\"string\"){");
		result.append("require(\"").append(mainModule).append("\")");
		if (execute) {
			result.append("[\"main\"]()");
		}
		result.append(";");
		result.append("}\n");

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
		result.append("if(typeof define===\"function\"&&define.amd){");
		result.append("require[\"config\"]({\"baseUrl\":\".\",\"paths\":{");
		// Append path definitions
		Joiner.on(',').appendTo(result, Iterables.concat(moduleDependencyPaths, externalDependencyPaths));
		// End config
		result.append("}});");
		result.append("}");
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
