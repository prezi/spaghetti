package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;

public class DefaultSpaghettiGeneratedSourceSet extends AbstractLanguageSourceSet implements SpaghettiGeneratedSourceSet {
	public DefaultSpaghettiGeneratedSourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti generated source", new DefaultSourceDirectorySet("source", fileResolver));
	}
}
