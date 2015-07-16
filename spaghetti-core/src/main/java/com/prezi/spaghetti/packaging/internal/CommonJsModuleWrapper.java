package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class CommonJsModuleWrapper extends AbstractModuleWrapper implements StructuredModuleWrapper {
	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		Map<String, String> modules = Maps.newLinkedHashMap();
		for (String dependency : Sets.newTreeSet(params.bundle.getDependentModules())) {
			modules.put(dependency, "require(__resolveDependency(\"" + dependency + "\"))");
		}

		StringBuilder result = new StringBuilder();
		result.append("module.exports=(function(){");
		result.append("var __resolveDependency=function(module){");
			result.append("if (global[\"spaghetti\"]&&global[\"spaghetti\"][\"config\"]&&global[\"spaghetti\"][\"config\"][\"paths\"]&&global[\"spaghetti\"][\"config\"][\"paths\"][module]){");
				result.append("return global[\"spaghetti\"][\"config\"][\"paths\"][module];");
			result.append("}else{");
				result.append("return module;");
			result.append("}");
		result.append("};");

		StringBuilder externalDependenciesDeclaration = new StringBuilder();
		for (Map.Entry<String, String> externalDependency : params.bundle.getExternalDependencies().entrySet()) {
			externalDependenciesDeclaration.append(
					String.format("var %s=require(__resolveDependency(\"%s\"));",
							externalDependency.getKey(),
							externalDependency.getValue())
			);
		}
		wrapModuleObject(result, params, "var baseUrl=__dirname;", externalDependenciesDeclaration, modules);
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
	protected StringBuilder makeConfig(StringBuilder result, Map<String, Set<String>> dependencyTree, Map<String, String> externals) {
		if (!externals.isEmpty()) {
			Collection<String> externalPaths = Collections2.transform(externals.entrySet(), new Function<Map.Entry<String, String>, String>() {
				@Override
				public String apply(Map.Entry<String, String> external) {
					return String.format("\"%s\":\"%s\"", external.getKey(), external.getValue());
				}
			});
			result.append("global[\"spaghetti\"]={\"config\":{\"paths\":{").append(Joiner.on(',').join(externalPaths)).append("}}};");
		}
		return result;
	}

	@Override
	public String getModulesDirectory() {
		return "node_modules";
	}
}
