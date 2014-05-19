package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.definition.ModuleType
import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
class ModuleBundleParameters {
	String name
	ModuleType type
	String definition
	String version
	String sourceBaseUrl
	String javaScript
	String sourceMap
	Set<String> dependentModules
	File resourcesDirectory
}
