package com.prezi.spaghetti.gradle

import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.language.base.FunctionalSourceSet
import org.gradle.language.base.internal.AbstractLanguageSourceSet

/**
 * Created by lptr on 15/02/14.
 */
class DefaultSpaghettiGeneratedSourceSet
		extends AbstractLanguageSourceSet
		implements SpaghettiGeneratedSourceSet {

	DefaultSpaghettiGeneratedSourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti generated source", new DefaultSourceDirectorySet("source", fileResolver))
	}
}
