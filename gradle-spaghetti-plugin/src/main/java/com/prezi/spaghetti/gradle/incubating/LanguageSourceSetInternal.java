package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.Task;

public interface LanguageSourceSetInternal extends LanguageSourceSet {

    /**
	 * A unique name for this source set across all functional source sets.
     */
    String getFullName();

    /**
     * Return true if the source set contains sources, or if the source set is generated.
     */
    boolean getMayHaveSources();

    Task getGeneratorTask();
}
