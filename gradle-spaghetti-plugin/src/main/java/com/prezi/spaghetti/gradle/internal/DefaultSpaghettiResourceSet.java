package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.AbstractLanguageSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory;

public class DefaultSpaghettiResourceSet extends AbstractLanguageSourceSet implements SpaghettiResourceSet {
	public DefaultSpaghettiResourceSet(String name, FunctionalSourceSet parent, FileResolver fileResolver) {
		super(name, parent, "Spaghetti resource", new DefaultSourceDirectorySet("resource", fileResolver, new DefaultDirectoryFileTreeFactory()));
	}
}
