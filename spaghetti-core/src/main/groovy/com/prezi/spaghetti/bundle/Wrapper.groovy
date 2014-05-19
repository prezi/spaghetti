package com.prezi.spaghetti.bundle

import static com.prezi.spaghetti.Generator.CONFIG
import static com.prezi.spaghetti.ReservedWords.BASE_URL
import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_WRAPPER_FUNCTION

/**
 * Created by lptr on 16/05/14.
 */
abstract class Wrapper {
	abstract String wrap(String moduleName, Collection<String> dependencies, String javaScript)
	abstract String makeApplication(String modulesRoot, Collection<String> moduleNames, String mainModule)

	public static final NOP = new Wrapper() {
		@Override
		String wrap(String moduleName, Collection<String> dependencies, String javaScript) {
			return javaScript
		}

		@Override
		String makeApplication(String modulesRoot, Collection<String> moduleNames, String mainModule) {
		}
	}

	public static final AMD = new Wrapper() {
		@Override
		String wrap(String moduleName, Collection<String> dependencies, String javaScript) {
			def moduleNamesWithRequire = (["require"] + dependencies).collect { "\"${it}\"" }
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
			result.append /**//**/ "getName:function(){"
			result.append /**//**//**/ "return \"${moduleName}\";"
			result.append /**//**/ "},"
			result.append /**//**/ "getResourceUrl:function(resource){"
			result.append /**//**//**/ "if(resource.substr(0,1)==\"/\"){"
			result.append /**//**//**//**/ "resource=resource.substr(1);"
			result.append /**//**//**/ "}"
			result.append /**//**//**/ "return baseUrl+resource;"
			result.append /**//**/ "}"
			result.append /**/ "};"
			result.append /**/ "var ${SPAGHETTI_WRAPPER_FUNCTION}=function(){"
			result.append /**//**/ "return arguments[0](${CONFIG});"
			result.append /**/ "};"
			result.append /**/ "return "
			result.append /**/ javaScript
			result.append "});"
			return result.toString()
		}

		@Override
		String makeApplication(String modulesRoot, Collection<String> moduleNames, String mainModule) {
			def result = new StringBuilder()
			result.append makeConfig(modulesRoot, moduleNames)
			result.append "require([\"${mainModule}\"],function(__mainModule){"
			result.append /**/ "__mainModule[\"${MODULE}\"][\"main\"]();"
			result.append "});"
			return result.toString()
		}

		private String makeConfig(String modulesRoot, Collection<String> moduleNames) {
			if (modulesRoot && !modulesRoot.endsWith("/")) {
				modulesRoot += "/"
			}
			def paths = moduleNames.collect { moduleName ->
				"\"${moduleName}\": \"${modulesRoot}${moduleName}/${moduleName}\""
			}
			return \
			 	"require[\"config\"]({" +
					"\"baseUrl\":\".\"," +
					"\"paths\":{" +
						paths.join(",") +
					"}" +
				"});"
		}
	}
}