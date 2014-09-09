package com.prezi.spaghetti.packaging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.ReservedWords.BASE_URL;
import static com.prezi.spaghetti.ReservedWords.CONFIG;
import static com.prezi.spaghetti.ReservedWords.GET_NAME_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.GET_RESOURCE_URL_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.MODULE;
import static com.prezi.spaghetti.ReservedWords.MODULES;
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION;

public class AmdWrapper implements Wrapper {

	@Override
	public String wrap(final String moduleName, Collection<String> dependencies, String javaScript) {
		Iterable<String> moduleNamesWithRequire = Iterables.transform(Iterables.concat(Arrays.asList("require"), Sets.newTreeSet(dependencies)), new Function<String, String>() {
			@Nullable
			@Override
			public String apply(@Nullable String input) {
				return '"' + input + '"';
			}
		});
		List<String> modules = Lists.newArrayList();
		int index = 0;
		for (String name : moduleNamesWithRequire) {
			modules.add(name + ":arguments[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("define([").append(Joiner.on(",").join(moduleNamesWithRequire)).append("],function(){");
		result.append("var moduleUrl=arguments[0][\"toUrl\"](\"").append(moduleName).append(".js\");");
		result.append("var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\")+1);");
		result.append("var ").append(CONFIG).append("={");
		result.append("\"").append(BASE_URL).append("\":baseUrl,");
		result.append("\"").append(MODULES).append("\":{");
		result.append(Joiner.on(',').join(modules));
		result.append("},");
		result.append(GET_NAME_FUNCTION).append(":function(){");
		result.append("return \"").append(moduleName).append("\";");
		result.append("},");
		result.append(GET_RESOURCE_URL_FUNCTION).append(":function(resource){");
		result.append("if(resource.substr(0,1)==\"/\"){");
		result.append("resource=resource.substr(1);");
		result.append("}");
		result.append("return baseUrl+resource;");
		result.append("}");
		result.append("};");
		result.append("var ").append(SPAGHETTI_WRAPPER_FUNCTION).append("=function(){");
		result.append("return arguments[0](").append(CONFIG).append(");");
		result.append("};");
		CommentUtils.appendAfterInitialComment(result, "return ", javaScript);
		result.append("});");
		return result.toString();
	}

	@Override
	public String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		result.append(makeConfig(baseUrl, modulesRoot, Sets.newTreeSet(dependencyTree.keySet())));
		if (mainModule != null) {
			result.append("require([\"").append(mainModule).append("\"],function(__mainModule){");
			if (execute) {
				result.append("__mainModule[\"").append(MODULE).append("\"][\"main\"]();");
			}

			result.append("});\n");
		}

		return result.toString();
	}

	private static String makeConfig(final String baseUrl, String modulesRoot, Collection<String> moduleNames) {
		final String normalizedModulesRoot = modulesRoot.endsWith("/") ? modulesRoot : modulesRoot + "/";

		Iterable<String> paths = Iterables.transform(moduleNames, new Function<String, String>() {
			@Override
			public String apply(String moduleName) {
				return "\"" + moduleName + "\": \"" + normalizedModulesRoot + moduleName + "/" + moduleName + "\"";
			}
		});
		return "require[\"config\"]({" + "\"baseUrl\":\"" + baseUrl + "\"," + "\"paths\":{" + Joiner.on(',').join(paths) + "}" + "});";
	}
}
