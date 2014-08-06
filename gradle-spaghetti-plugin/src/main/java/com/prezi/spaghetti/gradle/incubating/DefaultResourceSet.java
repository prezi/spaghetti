package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.file.SourceDirectorySet;

public class DefaultResourceSet extends AbstractLanguageSourceSet implements ResourceSet {

    public DefaultResourceSet(String name, SourceDirectorySet source, FunctionalSourceSet parent) {
		super(name, parent, "resources", source);
    }
}
