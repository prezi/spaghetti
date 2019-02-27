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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
				false);
		result.append(";");

		// AMD
		result.append("if(typeof define===\"function\"&&define.amd){");
		String baseUrlDeclaration = "var moduleUrl=arguments[0][\"toUrl\"](\"" + params.name + ".js\");"
			+ "baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\"));";
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
		result.append(baseUrlDeclaration);
		result.append("return");
		result.append("(__factory).apply({},[].slice.call(arguments,1)");
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

		// CommonJS
		result.append("}else if(typeof exports===\"object\"&&typeof exports.nodeName!==\"string\"){");
		result.append("baseUrl=__dirname;");
		result.append("module.exports=(__factory)");
		result.append("(");
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
		//result.append("return module.exports;");

		// Browser Globals
		Iterable<String> globalModuleDependencies = Stream.concat(
				params.dependencies.stream().map(dependency -> "this[\"" + dependency + "\"]"),
				params.lazyDependencies.stream().map(lazyDependency -> "function(){return Promise.resolve(this[\"" + lazyDependency + "\"].lazyModule);}")
		).collect(Collectors.toList());

		result.append("}else{");
		result.append("this[\"" + params.name + "\"]=");
		result
				.append("__factory(")
				.append(Joiner.on(",").join(Iterables.concat(params.externalDependencies.keySet(), globalModuleDependencies)))
				.append(");");
		result.append("}");

		result.append("}).call(this);");

		return result.toString();
	}

	@Override
	protected void makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
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
		if (execute) {
			result.append("}else{");
			result.append("this[\"").append(mainModule).append("\"]");
			result.append("[\"main\"]();");
		}
		result.append("}\n");
	}

	@Override
	protected void makeConfig(StringBuilder result, String modulesDirectory, Collection<String> dependencies, Map<String, String> externals) {
		result.append("if(typeof define===\"function\"&&define.amd){");
		RequireJsHelpers.makeConfig(result, modulesDirectory, dependencies, externals);
		result.append("}");
	}

	@Override
	public String makeJsonPathsMapping(Collection<String> dependencies, String modulesDirectory, Map<String, String> externals) {
		StringBuilder result = new StringBuilder();
		RequireJsHelpers.makePathsMapping(result, modulesDirectory, dependencies, externals);
		result.append("\n");
		return result.toString();
	}
}
