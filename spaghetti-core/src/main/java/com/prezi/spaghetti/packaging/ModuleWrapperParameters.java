package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;

public class ModuleWrapperParameters {
	public final String name;
	public final String version;
	public final String javaScript;
	public final SortedSet<String> dependencies;
	public final SortedMap<String, String> externalDependencies;

	public ModuleWrapperParameters(ModuleBundle bundle) throws IOException {
		this.name = bundle.getName();
		this.version = bundle.getVersion();
		this.javaScript = bundle.getJavaScript();
		this.dependencies = bundle.getDependentModules();
		this.externalDependencies = bundle.getExternalDependencies();
	}

	public ModuleWrapperParameters(String name, String version, String javaScript, SortedSet<String> dependencies, SortedMap<String, String> externalDependencies) {
		this.name = name;
		this.version = version;
		this.javaScript = javaScript;
		this.dependencies = dependencies;
		this.externalDependencies = externalDependencies;
	}
}
