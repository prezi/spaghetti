package com.prezi.spaghetti.gradle

import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.language.base.FunctionalSourceSet
import org.gradle.language.base.internal.AbstractLanguageSourceSet

class DefaultSpaghettiResourceSet
		extends AbstractLanguageSourceSet
		implements SpaghettiResourceSet {

	DefaultSpaghettiResourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti resource", new DefaultSourceDirectorySet("resource", fileResolver))
	}
}
