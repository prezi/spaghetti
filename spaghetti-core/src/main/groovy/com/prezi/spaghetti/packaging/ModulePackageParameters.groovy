package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleElement
import groovy.transform.TupleConstructor

@TupleConstructor
class ModulePackageParameters {
	public static final DEFAULT_ELEMENTS = EnumSet.of(ModuleBundleElement.javascript, ModuleBundleElement.javascript.resources)

	ModuleBundle bundle
	EnumSet<ModuleBundleElement> elements = DEFAULT_ELEMENTS
	void elements(ModuleBundleElement... elements) {
		this.elements = EnumSet.of(*elements)
	}
	Collection<String> prefixes = []
	Collection<String> suffixes = []
}
