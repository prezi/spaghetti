package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;

public class DefaultSpaghettiGeneratedSourceSet extends AbstractLanguageSourceSet implements SpaghettiGeneratedSourceSet {
	public DefaultSpaghettiGeneratedSourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti generated source", new DefaultSourceDirectorySet("source", fileResolver));
	}
}
