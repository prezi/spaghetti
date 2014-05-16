package com.prezi.spaghetti.bundle

import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
class ModuleBundleParameters {
	String name
	String definition
	String version
	String sourceBaseUrl
	String bundledJavaScript
	String sourceMap
	Set<String> dependentModules
	File resourcesDirectory
}
