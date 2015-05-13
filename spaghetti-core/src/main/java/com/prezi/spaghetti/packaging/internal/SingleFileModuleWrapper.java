package com.prezi.spaghetti.packaging.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class SingleFileModuleWrapper extends AbstractModuleWrapper {
	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {
		Map<String, String> modules = Maps.newLinkedHashMap();
		int index = params.bundle.getExternalDependencies().size();
		for (String dependency : Sets.newTreeSet(params.bundle.getDependentModules())) {
			modules.put(dependency, "dependencies[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("function(){");
		StringBuilder externalDependenciesDeclaration = new StringBuilder();
		int externalDependencyIdx = 0;
		for (String externalDependency : params.bundle.getExternalDependencies()) {
			externalDependenciesDeclaration.append(String.format("var %s=arguments[%d];", externalDependency, externalDependencyIdx));
			externalDependencyIdx++;
		}
		wrapModuleObject(result, params, "var baseUrl=__dirname;", externalDependenciesDeclaration, modules);
		result.append("}");
		return result.toString();
	}

	@Override
	protected StringBuilder makeConfig(StringBuilder result, Map<String, Set<String>> dependencyTree, Map<String, String> externals) {
		return result;
	}

	@Override
	protected StringBuilder makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("var mainModule=modules[\"").append(mainModule).append("\"][\"").append(MODULE).append("\"];");
		if (execute) {
			result.append("mainModule[\"main\"]();");
		}
		return result;
	}
}
