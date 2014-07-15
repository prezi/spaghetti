package com.prezi.spaghetti.gradle;

import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.internal.AbstractLanguageSourceSet;

public class DefaultSpaghettiSourceSet extends AbstractLanguageSourceSet implements SpaghettiSourceSet {
	public DefaultSpaghettiSourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti source", new DefaultSourceDirectorySet("source", fileResolver));
	}
}
