package com.prezi.spaghetti.bundle

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.prezi.spaghetti.Generator.CONFIG
import static com.prezi.spaghetti.ReservedWords.BASE_URL
import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

/**
 * Created by lptr on 22/05/14.
 */
protected class CommonJsWrapper implements Wrapper {
	private static final Logger logger = LoggerFactory.getLogger(CommonJsWrapper)

	@Override
	String wrap(String moduleName, Collection<String> dependencies, String javaScript) {
		def modules = []
		dependencies.eachWithIndex { dependency, index ->
			modules.add "\"${dependency}\":arguments[${index}]"
		}

		def result = new StringBuilder()
		result.append "module.exports=function(){"
		result.append /**/ "var ${CONFIG}={"
		result.append /**//**/ "\"${BASE_URL}\":__dirname,"
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
		result.append /**//**//**/ "return __dirname+resource;"
		result.append /**//**/ "}"
		result.append /**/ "};"
		result.append /**/ "var ${SPAGHETTI_WRAPPER_FUNCTION}=function(){"
		result.append /**//**/ "return arguments[0](${CONFIG});"
		result.append /**/ "};"
		result.append /**/ "return "
		result.append /**/ javaScript
		result.append "};"
		return result.toString()
	}

	@Override
	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute) {
		// Keep track of modules we need to load and their dependencies
		def remainingModules = dependencyTree.collectEntries(new TreeMap<String, Set<String>>()) { module, dependencies ->
			[ module, dependencies.toSet() ]
		}

		def result = new StringBuilder()
		result.append "var modules=[];"
		Map<String, Integer> moduleIndexes = [:]
		int moduleIndex = 0;

		while (remainingModules) {
			// Find a module that has no more remaining dependencies
			String module = remainingModules.find { name, dependencies -> dependencies.size() == 0 }?.key
			if (!module) {
				throw new IllegalStateException("Cyclic dependency detected among modules: ${remainingModules.keySet()}")
			}
			// Remove it from among the modules we still need to load
			remainingModules.remove(module)
			def dependencies = dependencyTree.get(module).sort()

			logger.debug "Processing {} with dependencies: {}", module, dependencies

			// Look up previously loaded module
			def dependencyInstances = dependencies.collect { "modules[${getIndex(moduleIndexes, it)}]" }
			result.append("modules.push(require(\"${baseUrl}/${modulesRoot}/${module}\")(${dependencyInstances.join(",")}));")
			moduleIndexes.put(module, moduleIndex++)

			// Remove module from among remaining dependencies of remaining other modules
			remainingModules.each { remainingModule, remainingDependencies ->
				remainingDependencies.remove(module)
			}
		}
		if (execute) {
			result.append "modules[${getIndex(moduleIndexes, mainModule)}][\"${MODULE}\"][\"main\"]();"
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
