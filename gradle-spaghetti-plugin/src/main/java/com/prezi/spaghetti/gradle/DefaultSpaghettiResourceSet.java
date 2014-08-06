package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;

public class DefaultSpaghettiResourceSet extends AbstractLanguageSourceSet implements SpaghettiResourceSet {
	public DefaultSpaghettiResourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti resource", new DefaultSourceDirectorySet("resource", fileResolver));
	}
}
