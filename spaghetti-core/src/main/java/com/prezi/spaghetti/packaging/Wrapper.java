package com.prezi.spaghetti.packaging;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface Wrapper {
	String wrap(ModuleWrappingParameters params) throws IOException;

	String makeApplication(Map<String, Set<String>> dependencyTree, String mainModule, boolean execute);
}
