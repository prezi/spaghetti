package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;

public class DefaultSpaghettiSourceSet extends AbstractLanguageSourceSet implements SpaghettiSourceSet {
	public DefaultSpaghettiSourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti source", new DefaultSourceDirectorySet("source", fileResolver));
	}
}
