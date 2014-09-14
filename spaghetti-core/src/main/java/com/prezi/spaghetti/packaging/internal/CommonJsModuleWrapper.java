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
			modules.put(dependency, "require(\"" + dependency + "\")");
		}

		StringBuilder result = new StringBuilder();
		result.append("module.exports=(function(){");
		wrapModuleObject(result, params, "var baseUrl=__dirname;", modules);
		result.append("})();");

		return result.toString();
	}

	@Override
	public String makeApplication(Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		if (mainModule != null && execute) {
			result.append("require(\"").append(mainModule).append("\")[\"").append(MODULE).append("\"][\"main\"]();\n");
		}

		return result.toString();
	}

	@Override
	public String getModulesDirectory() {
		return "node_modules";
	}
}
