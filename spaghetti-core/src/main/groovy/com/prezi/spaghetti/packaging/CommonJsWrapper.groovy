package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.internal.DependencyTreeResolver

import static com.prezi.spaghetti.ReservedWords.BASE_URL
import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.GET_NAME_FUNCTION
import static com.prezi.spaghetti.ReservedWords.GET_RESOURCE_URL_FUNCTION
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

class CommonJsWrapper implements Wrapper {
	@Override
	String wrap(String moduleName, Collection<String> dependencies, String javaScript) {
		def modules = []
		dependencies.sort().eachWithIndex { dependency, index ->
			modules.add "\"${dependency}\":arguments[${index}]"
		}

		def result = new StringBuilder()
		result.append "module.exports=function(){"
		result.append /**/ "var ${CONFIG}={"
		result.append /**//**/ "\"${BASE_URL}\":__dirname,"
		result.append /**//**/ "\"${MODULES}\":{"
		result.append /**//**//**/ modules.join(",")
		result.append /**//**/ "},"
		result.append /**//**/ "${GET_NAME_FUNCTION}:function(){"
		result.append /**//**//**/ "return \"${moduleName}\";"
		result.append /**//**/ "},"
		result.append /**//**/ "${GET_RESOURCE_URL_FUNCTION}:function(resource){"
		result.append /**//**//**/ "if(resource.substr(0,1)!=\"/\"){"
		result.append /**//**//**//**/ "resource=\"/\"+resource;"
		result.append /**//**//**/ "}"
		result.append /**//**//**/ "return __dirname+resource;"
		result.append /**//**/ "}"
		result.append /**/ "};"
		result.append /**/ "var ${SPAGHETTI_WRAPPER_FUNCTION}=function(){"
		result.append /**//**/ "return arguments[0](${CONFIG});"
		result.append /**/ "};"
		CommentUtils.appendAfterInitialComment(result, "return ", javaScript)
		result.append "};"
		return result.toString()
	}

	@Override
	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute) {
		// Keep track of modules we need to load and their dependencies
		def result = new StringBuilder()
		result.append "var modules=[];"
		int moduleIndex = 0;

		def moduleIndexes = DependencyTreeResolver.resolveDependencies(dependencyTree, new DependencyTreeResolver.DependencyProcessor<String, Integer>() {
			@Override
			Integer processDependency(String name, Collection<Integer> dependencies) {
				def dependencyInstances = dependencies.collect { "modules[$it]" }
				result.append("modules.push(require(\"${baseUrl}/${modulesRoot}/${name}\")(${dependencyInstances.join(",")}));")
				return moduleIndex++
			}
		})
		if (mainModule && execute) {
			result.append "modules[${getIndex(moduleIndexes, mainModule)}][\"${INSTANCE}\"][\"main\"]();\n"
		}
		return result.toString()
	}

	private static int getIndex(Map<String, Integer> moduleIndexes, String module) {
		if (!moduleIndexes.containsKey(module)) {
			throw new IllegalStateException("Module not loaded: ${module}")
		}
		return moduleIndexes.get(module)
	}
}
