package com.prezi.spaghetti.packaging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.internal.DependencyTreeResolver;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.prezi.spaghetti.ReservedWords.MODULE;

public class CommonJsWrapper extends AbstractWrapper {
	@Override
	public String wrap(ModuleWrappingParameters params) throws IOException {
		Map<String, String> modules = Maps.newLinkedHashMap();
		int index = 0;
		for (String dependency : Sets.newTreeSet(params.bundle.getDependentModules())) {
			modules.put(dependency, "args[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("module.exports=function(){");
		wrapModuleObject(result, params, "__dirname", modules);
		result.append("};");

		return result.toString();
	}

	@Override
	public String makeApplication(final String baseUrl, final String modulesRoot, Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		// Keep track of modules we need to load and their dependencies
		final StringBuilder result = new StringBuilder();
		result.append("var modules=[];");
		final AtomicInteger moduleIndex = new AtomicInteger(0);

		Map<String, Integer> moduleIndexes = DependencyTreeResolver.resolveDependencies(dependencyTree, new DependencyTreeResolver.DependencyProcessor<String, Integer>() {
			@Override
			public Integer processDependency(String name, Collection<Integer> dependencies) {
				Collection<String> dependencyInstances = Collections2.transform(dependencies, new Function<Integer, String>() {
					@Override
					public String apply(Integer input) {
						return "modules[" + input + "]";
					}
				});
				result.append("modules.push(require(\"").append(baseUrl).append("/").append(modulesRoot).append("/").append(name).append("\")(").append(Joiner.on(',').join(dependencyInstances)).append("));");
				return moduleIndex.getAndIncrement();
			}
		});
		if (mainModule != null && execute) {
			result.append("modules[").append(CommonJsWrapper.getIndex(moduleIndexes, mainModule)).append("][\"").append(MODULE).append("\"][\"main\"]();\n");
		}

		return result.toString();
	}

	private static int getIndex(Map<String, Integer> moduleIndexes, final String module) {
		if (!moduleIndexes.containsKey(module)) {
			throw new IllegalStateException("Module not loaded: " + module);
		}

		return moduleIndexes.get(module);
	}
}
