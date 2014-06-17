package com.prezi.spaghetti.bundle

import groovy.transform.TupleConstructor

@TupleConstructor
class ModuleBundleParameters {
	String name
	String definition
	String version
	String sourceBaseUrl
	String javaScript
	String sourceMap
	SortedSet<String> dependentModules
	File resourcesDirectory
}
