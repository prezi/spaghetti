package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.Action;
import org.gradle.api.Incubating;
import org.gradle.api.Named;
import org.gradle.api.Task;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.internal.HasInternalProtocol;

/**
 * A set of sources for a programming language.
 */
@Incubating
@HasInternalProtocol
public interface LanguageSourceSet extends Named, BuildableModelElement {
	// TODO: do we want to keep using SourceDirectorySet in the new API?
    // would feel more natural if dirs could be added directly to LanguageSourceSet
    // could also think about extending SourceDirectorySet

    /**
     * The source files.
     */
    SourceDirectorySet getSource();

    /**
     * Configure the sources
     */
    void source(Action<? super SourceDirectorySet> config);

    void generatedBy(Task generatorTask);
}
