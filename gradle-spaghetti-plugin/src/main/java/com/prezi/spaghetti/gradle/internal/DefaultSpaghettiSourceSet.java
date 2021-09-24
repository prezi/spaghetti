package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory;
import org.gradle.api.model.ObjectFactory;

public class DefaultSpaghettiSourceSet extends AbstractLanguageSourceSet implements SpaghettiSourceSet {
	public DefaultSpaghettiSourceSet(String name, FunctionalSourceSet parent, ObjectFactory objectFactory) {
		super(name, parent, "Spaghetti source", objectFactory.sourceDirectorySet("source", "source"));
	}
}
