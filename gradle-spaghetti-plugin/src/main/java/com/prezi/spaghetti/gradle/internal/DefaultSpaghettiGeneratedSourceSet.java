package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory;
import org.gradle.api.model.ObjectFactory;

public class DefaultSpaghettiGeneratedSourceSet extends AbstractLanguageSourceSet implements SpaghettiGeneratedSourceSet {
	public DefaultSpaghettiGeneratedSourceSet(String name, FunctionalSourceSet parent, ObjectFactory objectFactory) {
		super(name, parent, "Spaghetti generated source", objectFactory.sourceDirectorySet("source", "source"));
	}
}
