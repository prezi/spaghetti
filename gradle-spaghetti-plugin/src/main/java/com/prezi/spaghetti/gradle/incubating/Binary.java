package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.Incubating;
import org.gradle.api.Named;
import org.gradle.internal.HasInternalProtocol;

/**
 * A physical binary artifact, which can run on a particular platform or runtime.
 */
@Incubating
@HasInternalProtocol
public interface Binary extends BuildableModelElement, Named {
	/**
     * Returns a human-consumable display name for this binary.
     */
    String getDisplayName();
}
