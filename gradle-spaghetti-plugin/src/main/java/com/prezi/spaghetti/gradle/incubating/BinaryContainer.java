package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer;
import org.gradle.api.Incubating;

/**
 * A container for project binaries, which represent physical artifacts that can run on a particular platform or runtime.
 * Added to a project by the {@link org.gradle.language.base.plugins.LanguageBasePlugin}.
 */
@Incubating
public interface BinaryContainer extends ExtensiblePolymorphicDomainObjectContainer<Binary> {}
