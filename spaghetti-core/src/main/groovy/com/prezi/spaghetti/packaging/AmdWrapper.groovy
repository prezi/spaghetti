package com.prezi.spaghetti.packaging

import static com.prezi.spaghetti.ReservedWords.BASE_URL
import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.GET_NAME_FUNCTION
import static com.prezi.spaghetti.ReservedWords.GET_RESOURCE_URL_FUNCTION
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

class AmdWrapper implements Wrapper {
	@Override
	String wrap(String moduleName, Collection<String> dependencies, String javaScript) {
		def moduleNamesWithRequire = (["require"] + dependencies.sort()).collect { "\"${it}\"" }
		def modules = []
		moduleNamesWithRequire.eachWithIndex { name, index ->
			modules.add "${name}:arguments[${index}]"
		}

		def result = new StringBuilder()
		result.append "define([${moduleNamesWithRequire.join(",")}],function(){"
		result.append /**/ "var moduleUrl=arguments[0][\"toUrl\"](\"${moduleName}.js\");"
		result.append /**/ "var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf(\"/\")+1);"
		result.append /**/ "var ${CONFIG}={"
		result.append /**//**/ "\"${BASE_URL}\":baseUrl,"
		result.append /**//**/ "\"${MODULES}\":{"
		result.append /**//**//**/ modules.join(",")
		result.append /**//**/ "},"
		result.append /**//**/ "${GET_NAME_FUNCTION}:function(){"
		result.append /**//**//**/ "return \"${moduleName}\";"
		result.append /**//**/ "},"
		result.append /**//**/ "${GET_RESOURCE_URL_FUNCTION}:function(resource){"
		result.append /**//**//**/ "if(resource.substr(0,1)==\"/\"){"
		result.append /**//**//**//**/ "resource=resource.substr(1);"
		result.append /**//**//**/ "}"
		result.append /**//**//**/ "return baseUrl+resource;"
		result.append /**//**/ "}"
		result.append /**/ "};"
		result.append /**/ "var ${SPAGHETTI_WRAPPER_FUNCTION}=function(){"
		result.append /**//**/ "return arguments[0](${CONFIG});"
		result.append /**/ "};"
		CommentUtils.appendAfterInitialComment(result, "return ", javaScript)
		result.append "});"
		return result.toString()
	}

	@Override
	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute) {
		def result = new StringBuilder()
		result.append makeConfig(baseUrl, modulesRoot, dependencyTree.keySet().sort())
		if (mainModule) {
			result.append "require([\"${mainModule}\"],function(__mainModule){"
			if (execute) {
				result.append "__mainModule[\"${INSTANCE}\"][\"main\"]();"
			}
			result.append "});\n"
		}
		return result.toString()
	}

	private static String makeConfig(String baseUrl, String modulesRoot, Collection<String> moduleNames) {
		if (modulesRoot && !modulesRoot.endsWith("/")) {
			modulesRoot += "/"
		}
		def paths = moduleNames.collect { moduleName ->
			"\"${moduleName}\": \"${modulesRoot}${moduleName}/${moduleName}\""
		}
		return \
		 	"require[\"config\"]({" +
				"\"baseUrl\":\"${baseUrl}\"," +
				"\"paths\":{" +
					paths.join(",") +
				"}" +
			"});"
	}
}
