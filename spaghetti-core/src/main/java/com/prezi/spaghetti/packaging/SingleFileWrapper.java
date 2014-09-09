package com.prezi.spaghetti.packaging;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.ReservedWords.MODULE;

public class SingleFileWrapper extends AbstractWrapper {
	@Override
	public String wrap(ModuleWrappingParameters params) throws IOException {
		Map<String, String> modules = Maps.newLinkedHashMap();
		int index = 0;
		for (String dependency : Sets.newTreeSet(params.bundle.getDependentModules())) {
			modules.put(dependency, "args[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("function(){");
		wrapModuleObject(result, params, "__dirname", modules);
		result.append("}");
		return result.toString();
	}

	@Override
	public String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		if (execute) {
			result.append("modules[\"").append(mainModule).append("\"][\"").append(MODULE).append("\"][\"main\"]();");
		}

		return result.toString();
	}
}
