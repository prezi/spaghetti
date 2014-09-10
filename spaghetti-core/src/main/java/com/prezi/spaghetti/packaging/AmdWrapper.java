package com.prezi.spaghetti.packaging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.ReservedWords.MODULE;

public class AmdWrapper extends AbstractWrapper implements StructuredWrapper {

	@Override
	public String wrap(ModuleWrappingParameters params) throws IOException {
		Iterable<String> moduleNamesWithRequire = Iterables.concat(Arrays.asList("require"), Sets.newTreeSet(params.bundle.getDependentModules()));
		Map<String, String> modules = Maps.newLinkedHashMap();
		int index = 0;
		for (String name : moduleNamesWithRequire) {
			modules.put(name, "args[" + index + "]");
			index++;
		}

		String baseUrlDeclaration = "var moduleUrl=args[0][\"toUrl\"](\"" + params.bundle.getName() + ".js\");"
			+ "var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\"));";

		StringBuilder result = new StringBuilder();
		result.append("define([\"").append(Joiner.on("\",\"").join(moduleNamesWithRequire)).append("\"],function(){");
		wrapModuleObject(result, params, baseUrlDeclaration, modules);
		result.append("});");
		return result.toString();
	}

	@Override
	public String makeApplication(Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		result.append(makeConfig(getModulesDirectory(), Sets.newTreeSet(dependencyTree.keySet())));
		if (mainModule != null) {
			result.append("require([\"").append(mainModule).append("\"],function(__mainModule){");
			if (execute) {
				result.append("__mainModule[\"").append(MODULE).append("\"][\"main\"]();");
			}

			result.append("});\n");
		}

		return result.toString();
	}

	@Override
	public String getModulesDirectory() {
		return "modules";
	}

	private static String makeConfig(String modulesRoot, Collection<String> moduleNames) {
		final String normalizedModulesRoot = modulesRoot.endsWith("/") ? modulesRoot : modulesRoot + "/";

		Iterable<String> paths = Iterables.transform(moduleNames, new Function<String, String>() {
			@Override
			public String apply(String moduleName) {
				return "\"" + moduleName + "\": \"" + normalizedModulesRoot + moduleName + "/" + moduleName + "\"";
			}
		});
		return "require[\"config\"]({" + "\"baseUrl\":\".\"," + "\"paths\":{" + Joiner.on(',').join(paths) + "}" + "});";
	}
}
