package com.prezi.spaghetti.packaging.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class CommonJsModuleWrapper extends AbstractModuleWrapper implements StructuredModuleWrapper {
	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		Map<String, String> modules = Maps.newLinkedHashMap();
		for (String dependency : Sets.newTreeSet(params.bundle.getDependentModules())) {
			modules.put(dependency, "require(getPath(\"" + dependency + "\"))");
		}

		StringBuilder result = new StringBuilder();
		result.append("module.exports=(function(){");
		result.append("var getPath=function(module){");
			result.append("if (global[\"spaghetti\"]&&global[\"spaghetti\"][\"config\"]&&global[\"spaghetti\"][\"config\"][\"paths\"]&&global[\"spaghetti\"][\"config\"][\"paths\"][module]){");
				result.append("return global[\"spaghetti\"][\"config\"][\"paths\"][module];");
			result.append("}else{");
				result.append("return module;");
			result.append("}");
		result.append("};");

		StringBuilder externalDependenciesDeclaration = new StringBuilder();
		for (String externalDependency : params.bundle.getExternalDependencies()) {
			externalDependenciesDeclaration.append(
					String.format("var %s=require(getPath(\"%s\"));", externalDependency, externalDependency)
			);
		}
		wrapModuleObject(result, params, "var baseUrl=__dirname;", externalDependenciesDeclaration, modules);
		result.append("})();");

		return result.toString();
	}

	@Override
	public String makeApplication(Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		if (mainModule != null) {
			result.append("var mainModule=require(\"").append(mainModule).append("\")[\"").append(MODULE).append("\"];");
			if (execute) {
				result.append("mainModule[\"main\"]();\n");
			}
		}

		return result.toString();
	}

	@Override
	public String getModulesDirectory() {
		return "node_modules";
	}
}
