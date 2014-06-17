package com.prezi.spaghetti.packaging

interface Wrapper {
	String wrap(String moduleName, Collection<String> dependencies, String javaScript)
	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute)
}
