package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle
import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
class ApplicationPackageParameters {
	public static final String DEFAULT_BASE_URL = "."
	public static final String DEFAULT_MODULES_DIRECTORY = "modules"
	public static final String DEFAULT_APPLICATION_NAME = "application.js"
	public static final boolean DEFAULT_EXECUTE = true

	Set<ModuleBundle> bundles
	String modulesDirectory = DEFAULT_MODULES_DIRECTORY
	String baseUrl = DEFAULT_BASE_URL
	String applicationName = DEFAULT_APPLICATION_NAME
	String mainModule
	boolean execute = DEFAULT_EXECUTE
	Collection<String> prefixes = []
	Collection<String> suffixes = []
}
