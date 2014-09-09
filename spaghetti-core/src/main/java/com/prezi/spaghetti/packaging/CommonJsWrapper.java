package com.prezi.spaghetti.packaging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.internal.DependencyTreeResolver;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.prezi.spaghetti.ReservedWords.BASE_URL;
import static com.prezi.spaghetti.ReservedWords.CONFIG;
import static com.prezi.spaghetti.ReservedWords.GET_NAME_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.GET_RESOURCE_URL_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.MODULE;
import static com.prezi.spaghetti.ReservedWords.MODULES;
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION;

public class CommonJsWrapper implements Wrapper {
	@Override
	public String wrap(final String moduleName, Collection<String> dependencies, String javaScript) {
		List<String> modules = Lists.newArrayList();
		int index = 0;
		for (String dependency : Sets.newTreeSet(dependencies)) {
			modules.add("\"" + dependency + "\":arguments[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("module.exports=function(){");
		result.append("var ").append(CONFIG).append("={");
		result.append("\"").append(BASE_URL).append("\":__dirname,");
		result.append("\"").append(MODULES).append("\":{");
		result.append(Joiner.on(',').join(modules));
		result.append("},");
		result.append(GET_NAME_FUNCTION + ":function(){");
		result.append("return \"").append(moduleName).append("\";");
		result.append("},");
		result.append(GET_RESOURCE_URL_FUNCTION + ":function(resource){");
		result.append("if(resource.substr(0,1)!=\"/\"){");
		result.append("resource=\"/\"+resource;");
		result.append("}");
		result.append("return __dirname+resource;");
		result.append("}");
		result.append("};");
		result.append("var ").append(SPAGHETTI_WRAPPER_FUNCTION).append("=function(){");
		result.append("return arguments[0](").append(CONFIG).append(");");
		result.append("};");
		CommentUtils.appendAfterInitialComment(result, "return ", javaScript);
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
