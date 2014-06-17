package com.prezi.spaghetti.packaging;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.ReservedWords.BASE_URL;
import static com.prezi.spaghetti.ReservedWords.CONFIG;
import static com.prezi.spaghetti.ReservedWords.INSTANCE;
import static com.prezi.spaghetti.ReservedWords.MODULES;
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION;

public class SingleFileWrapper implements Wrapper {
	@Override
	@SuppressWarnings("StringBufferReplaceableByString")
	public String wrap(final String moduleName, Collection<String> dependencies, String javaScript) {
		List<String> modules = Lists.newArrayList();
		int index = 0;
		for (String dependency : dependencies) {
			modules.add("\"" + dependency + "\":arguments[" + index + "]");
			index++;
		}

		StringBuilder result = new StringBuilder();
		result.append("function(){");
		result.append("var ").append(CONFIG).append("={");
		result.append("\"").append(BASE_URL).append("\":__dirname+\"/").append(moduleName).append("\",");
		result.append("\"").append(MODULES).append("\":{");
		result.append(Joiner.on(',').join(modules));
		result.append("},");
		result.append("getName:function(){");
		result.append("return \"").append(moduleName).append("\";");
		result.append("},");
		result.append("getResourceUrl:function(resource){");
		result.append("if(resource.substr(0,1)!=\"/\"){");
		result.append("resource=\"/\"+resource;");
		result.append("}");
		result.append("return __dirname+\"/").append(moduleName).append("\"+resource;");
		result.append("}");
		result.append("};");
		result.append("var ").append(SPAGHETTI_WRAPPER_FUNCTION).append("=function(){");
		result.append("return arguments[0](").append(CONFIG).append(");");
		result.append("};");
		result.append("return ");
		result.append(javaScript);
		result.append("}");
		return result.toString();
	}

	@Override
	public String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, final String mainModule, boolean execute) {
		StringBuilder result = new StringBuilder();
		if (execute) {
			result.append("modules[\"").append(mainModule).append("\"][\"").append(INSTANCE).append("\"][\"main\"]();");
		}

		return result.toString();
	}

}
