package com.prezi.spaghetti.packaging;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Wrapper {
	String wrap(String moduleName, Collection<String> dependencies, String javaScript);

	String makeApplication(String baseUrl, String modulesRoot, Map<String, Set<String>> dependencyTree, String mainModule, boolean execute);
}
