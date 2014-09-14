package com.prezi.spaghetti.gradle.internal.incubating;

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer;
import org.gradle.api.Incubating;
import org.gradle.api.Named;

/**
 * A container holding {@link LanguageSourceSet}s with a similar function
 * (production code, test code, etc.).
 */
@Incubating
public interface FunctionalSourceSet extends ExtensiblePolymorphicDomainObjectContainer<LanguageSourceSet>, Named {}
