package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Strings;
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
	public String makeApplication(Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute, Map<String, String> parameters) {
		StringBuilder result = new StringBuilder();
		if (!Strings.isNullOrEmpty(mainModule)) {
			result.append("var mainModule=modules[\"").append(mainModule).append("\"][\"").append(MODULE).append("\"];");
			if (execute) {
				result.append("mainModule[\"main\"](");
					makeParameters(result, parameters);
				result.append(");");
			}
		}

		return result.toString();
	}
}
