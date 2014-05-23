package com.prezi.spaghetti.packaging

/**
 * Created by lptr on 16/05/14.
 */
interface Wrapper {
	String wrap(String moduleName, Collection<String> dependencies, String javaScript)
	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute)
}
