package com.prezi.spaghetti.packaging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.prezi.spaghetti.Version;

import java.io.IOException;
import java.util.Map;

import static com.prezi.spaghetti.ReservedWords.DEPENDENCIES;
import static com.prezi.spaghetti.ReservedWords.GET_NAME_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.GET_RESOURCE_URL_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.GET_SPAGHETTI_VERSION;
import static com.prezi.spaghetti.ReservedWords.GET_VERSION;
import static com.prezi.spaghetti.packaging.CommentUtils.appendAfterInitialComment;

public abstract class AbstractWrapper implements Wrapper {
	protected void wrapModuleObject(StringBuilder builder, ModuleWrappingParameters params, String baseUrlDeclaration, Map<String, String> dependencies) throws IOException {
		Iterable<String> dependencyLines = Iterables.transform(dependencies.entrySet(), new Function<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Map.Entry<String, String> entry) {
				return "\"" + entry.getKey() + "\":" + entry.getValue();
			}
		});
		String moduleName = params.bundle.getName();

		builder.append("var module=(function(dependencies){");
			builder.append("return function(init){");
				builder.append("return init.call({},(function(){");
					builder.append(baseUrlDeclaration);
					builder.append("return{");
						builder.append(GET_SPAGHETTI_VERSION).append(":function(){");
							builder.append("return \"").append(Version.SPAGHETTI_BUILD).append("\";");
						builder.append("},");
						builder.append(GET_NAME_FUNCTION).append(":function(){");
							builder.append("return \"").append(moduleName).append("\";");
						builder.append("},");
						builder.append(GET_VERSION).append(":function(){");
							builder.append("return \"").append(params.bundle.getVersion()).append("\";");
						builder.append("},");
						builder.append(GET_RESOURCE_URL_FUNCTION).append(":function(resource){");
							builder.append("if(resource.substr(0,1)!=\"/\"){");
								builder.append("resource=\"/\"+resource;");
							builder.append("}");
							builder.append("return baseUrl+resource;");
						builder.append("},");
						builder.append("\"").append(DEPENDENCIES).append("\":{");
							builder.append(Joiner.on(',').join(dependencyLines));
						builder.append("}");
					builder.append("};");
				builder.append("})());");
			builder.append("};");
		builder.append("})(arguments);");
		appendAfterInitialComment(builder, "return{\"module\":", params.bundle.getJavaScript());
		builder.append(",");
		builder.append("\"version\":\"").append(params.bundle.getVersion()).append("\",");
		builder.append("\"spaghettiVersion\":\"").append(Version.SPAGHETTI_BUILD).append("\"");
		builder.append("};");
	}
}
