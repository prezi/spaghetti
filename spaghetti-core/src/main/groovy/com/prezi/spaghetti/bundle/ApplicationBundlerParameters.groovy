package com.prezi.spaghetti.bundle

import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
class ApplicationBundlerParameters {
	Set<ModuleBundle> bundles
	String mainModule
	Wrapper wrapper
}
