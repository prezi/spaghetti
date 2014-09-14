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
		int index = 0;
		for (String dependency : Sets.newTreeSet(params.bundle.getDependentModules())) {
			modules.put(dependency, "dependencies[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("function(){");
		wrapModuleObject(result, params, "var baseUrl=__dirname;", modules);
		result.append("}");
		return result.toString();
	}

	@Override
	public String makeApplication(Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		if (execute) {
			result.append("modules[\"").append(mainModule).append("\"][\"").append(MODULE).append("\"][\"main\"]();");
		}

		return result.toString();
	}
}
