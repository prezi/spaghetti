package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle
import spock.lang.Specification

class WrapperTestBase extends Specification {
	def mockParams(String name, String version, Collection<String> dependencies, Collection<String> externalDependencies, String javaScript) {
		def mockBundle = Mock(ModuleBundle)
		mockBundle.name >> name
		mockBundle.version >> version
		mockBundle.dependentModules >> dependencies
		mockBundle.externalDependencies >> (externalDependencies as SortedSet)
		mockBundle.javaScript >> javaScript
		return new ModuleWrapperParameters(mockBundle)
	}
}
