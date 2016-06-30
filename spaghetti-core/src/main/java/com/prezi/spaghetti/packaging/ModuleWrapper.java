package com.prezi.spaghetti.packaging;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface ModuleWrapper {
	String wrap(ModuleWrapperParameters params) throws IOException;

	String makeApplication(Map<String, Set<String>> dependencyTree, String modulesDirectory, String mainModule, boolean execute, Map<String, String> externals);
}
