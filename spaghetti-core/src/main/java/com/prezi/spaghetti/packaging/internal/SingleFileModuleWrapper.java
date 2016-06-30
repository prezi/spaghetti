package com.prezi.spaghetti.packaging.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class SingleFileModuleWrapper extends AbstractModuleWrapper {
	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {

		StringBuilder result = new StringBuilder();
		result.append("function(){");
		result.append("var baseUrl=__dirname;");
		result.append("(");
		wrapModuleObject(
				result,
				params,
				params.dependencies,
				params.externalDependencies.keySet()
		);
		result.append(").call({},arguments);");
		result.append("}");
		return result.toString();
	}

	@Override
	protected StringBuilder makeConfig(StringBuilder result, String modulesDirectory, Map<String, Set<String>> dependencyTree, Map<String, String> externals) {
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
