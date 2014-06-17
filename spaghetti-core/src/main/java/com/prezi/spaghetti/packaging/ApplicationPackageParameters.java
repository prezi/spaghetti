package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

import java.util.Collection;
import java.util.Set;

public class ApplicationPackageParameters {
	public static final String DEFAULT_BASE_URL = ".";
	public static final String DEFAULT_MODULES_DIRECTORY = "modules";
	public static final String DEFAULT_APPLICATION_NAME = "application.js";
	public static final boolean DEFAULT_EXECUTE = true;

	public final Set<ModuleBundle> bundles;
	public final String modulesDirectory;
	public final String baseUrl;
	public final String applicationName;
	public final String mainModule;
	public final boolean execute;
	public final Collection<String> prefixes;
	public final Collection<String> suffixes;

	public ApplicationPackageParameters(Set<ModuleBundle> bundles, String modulesDirectory, String baseUrl, String applicationName, String mainModule, boolean execute, Collection<String> prefixes, Collection<String> suffixes) {
		this.bundles = bundles;
		this.modulesDirectory = modulesDirectory;
		this.baseUrl = baseUrl;
		this.applicationName = applicationName;
		this.mainModule = mainModule;
		this.execute = execute;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}
}
