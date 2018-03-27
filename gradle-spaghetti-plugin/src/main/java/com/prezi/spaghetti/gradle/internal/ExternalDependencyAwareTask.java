package com.prezi.spaghetti.gradle.internal;

import java.util.Map;

import org.gradle.api.Task;

public interface ExternalDependencyAwareTask extends Task {
	public void externalDependencies(Map<String, String> externalDependencies);
	public void externalDependency(String importName, String dependencyName);
	public void externalDependency(String shorthand);
}
