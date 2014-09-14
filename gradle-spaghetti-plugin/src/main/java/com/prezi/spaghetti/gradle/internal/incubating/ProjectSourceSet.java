package com.prezi.spaghetti.gradle.internal.incubating;

import org.gradle.api.Incubating;
import org.gradle.api.NamedDomainObjectContainer;

/**
 * A container of {@link FunctionalSourceSet}s. Added to a project by the
 * {@link org.gradle.language.base.plugins.LanguageBasePlugin}.
 */
@Incubating
public interface ProjectSourceSet extends NamedDomainObjectContainer<FunctionalSourceSet> {}
