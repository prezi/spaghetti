package com.prezi.spaghetti.packaging

import com.google.common.collect.ImmutableSortedMap
import com.prezi.spaghetti.bundle.ModuleBundle
import spock.lang.Specification

class WrapperTestBase extends Specification {
	def mockParams(String name, String version, Collection<String> dependencies,  Collection<String> lazyDependencies, Map<String, String> externalDependencies, String javaScript) {
		def mockBundle = Mock(ModuleBundle)
		mockBundle.name >> name
		mockBundle.version >> version
		mockBundle.dependentModules >> (dependencies as SortedSet)
		mockBundle.lazyDependentModules >> (lazyDependencies as SortedSet)
		mockBundle.externalDependencies >> ImmutableSortedMap.copyOf(externalDependencies)
		mockBundle.javaScript >> javaScript
		return new ModuleWrapperParameters(mockBundle)
	}
}
