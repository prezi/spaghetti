package com.prezi.spaghetti.bundle

import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
class ApplicationBundlerParameters {
	public static final String DEFAULT_BASE_URL = "."
	public static final String DEFAULT_MODULES_DIRECTORY = "modules"
	public static final String DEFAULT_APPLICATION_NAME = "application.js"
	public static final boolean DEFAULT_EXECUTE = true
	public static final ApplicationType DEFAULT_APPLICATION_TYPE = ApplicationType.COMMON_JS

	Set<ModuleBundle> bundles
	String modulesDirectory = DEFAULT_MODULES_DIRECTORY
	String baseUrl = DEFAULT_BASE_URL
	String applicationName = DEFAULT_APPLICATION_NAME
	String mainModule
	boolean execute = DEFAULT_EXECUTE
	ApplicationType type = DEFAULT_APPLICATION_TYPE
}
