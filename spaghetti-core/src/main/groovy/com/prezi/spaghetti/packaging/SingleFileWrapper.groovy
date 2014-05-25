package com.prezi.spaghetti.packaging

import static com.prezi.spaghetti.ReservedWords.BASE_URL
import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

/**
 * Created by lptr on 25/05/14.
 */
class SingleFileWrapper implements Wrapper {
	@Override
	String wrap(String moduleName, Collection<String> dependencies, String javaScript) {
		def modules = []
		dependencies.sort().eachWithIndex { dependency, index ->
			modules.add "\"${dependency}\":arguments[${index}]"
		}

		def result = new StringBuilder()
		result.append "function(){"
		result.append /**/ "var ${CONFIG}={"
		result.append /**//**/ "\"${BASE_URL}\":__dirname+\"/${moduleName}\","
		result.append /**//**/ "\"${MODULES}\":{"
		result.append /**//**//**/ modules.join(",")
		result.append /**//**/ "},"
		result.append /**//**/ "getName:function(){"
		result.append /**//**//**/ "return \"${moduleName}\";"
		result.append /**//**/ "},"
		result.append /**//**/ "getResourceUrl:function(resource){"
		result.append /**//**//**/ "if(resource.substr(0,1)!=\"/\"){"
		result.append /**//**//**//**/ "resource=\"/\"+resource;"
		result.append /**//**//**/ "}"
		result.append /**//**//**/ "return __dirname+\"/${moduleName}\"+resource;"
		result.append /**//**/ "}"
		result.append /**/ "};"
		result.append /**/ "var ${SPAGHETTI_WRAPPER_FUNCTION}=function(){"
		result.append /**//**/ "return arguments[0](${CONFIG});"
		result.append /**/ "};"
		result.append /**/ "return "
		result.append /**/ javaScript
		result.append "}"
		return result.toString()
	}

	@Override
	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute) {
		def result = new StringBuilder()
		if (execute) {
			result.append "modules[\"${mainModule}\"][\"${INSTANCE}\"][\"main\"]();"
		}
		return result.toString()
	}
}
