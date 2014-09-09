package com.prezi.spaghetti.packaging;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.prezi.spaghetti.Version;

import java.io.IOException;
import java.util.Map;

import static com.prezi.spaghetti.ReservedWords.BASE_URL;
import static com.prezi.spaghetti.ReservedWords.GET_NAME_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.GET_RESOURCE_URL_FUNCTION;
import static com.prezi.spaghetti.ReservedWords.GET_SPAGHETTI_VERSION;
import static com.prezi.spaghetti.ReservedWords.GET_VERSION;
import static com.prezi.spaghetti.ReservedWords.MODULES;
import static com.prezi.spaghetti.packaging.CommentUtils.appendAfterInitialComment;

public abstract class AbstractWrapper implements Wrapper {
	protected void wrapModuleObject(StringBuilder builder, ModuleWrappingParameters params, String baseUrl, Map<String, String> dependencies) throws IOException {
		Iterable<String> modules = Iterables.transform(dependencies.entrySet(), new Function<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Map.Entry<String, String> entry) {
				return "\"" + entry.getKey() + "\":" + entry.getValue();
			}
		});

		appendAfterInitialComment(builder, "return{\"module\":(", params.bundle.getJavaScript());
		builder.append("\n)");
		builder.append(".call({},(function(args){");

		String moduleName = params.bundle.getName();
		builder.append("var baseUrl=").append(baseUrl).append(";");
		builder.append("return{");
		// '"getVersion":function(){return "1.0"},',
		// '"spaghettiVersion":function(){return "' + Version.SPAGHETTI_BUILD + '";},',
		builder.append("\"").append(GET_VERSION).append("\":function(){return \"").append(params.bundle.getVersion()).append("\";},");
		builder.append("\"").append(GET_SPAGHETTI_VERSION).append("\":function(){return \"").append(Version.SPAGHETTI_BUILD).append("\";},");
		builder.append("\"").append(BASE_URL).append("\":baseUrl,");
		builder.append("\"").append(MODULES).append("\":{");
		builder.append(Joiner.on(',').join(modules));
		builder.append("},");
		builder.append(GET_NAME_FUNCTION).append(":function(){");
		builder.append("return \"").append(moduleName).append("\";");
		builder.append("},");
		builder.append(GET_RESOURCE_URL_FUNCTION).append(":function(resource){");
		builder.append("if(resource.substr(0,1)!=\"/\"){");
		builder.append("resource=\"/\"+resource;");
		builder.append("}");
		builder.append("return baseUrl+resource;");
		builder.append("}");
		builder.append("};");
		builder.append("})(arguments)),");
		builder.append("\"version\":\"").append(params.bundle.getVersion()).append("\",");
		builder.append("\"spaghettiVersion\":\"").append(Version.SPAGHETTI_BUILD).append("\"");
		builder.append("};");
	}
}
