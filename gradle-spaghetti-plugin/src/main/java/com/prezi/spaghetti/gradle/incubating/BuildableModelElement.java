package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.Buildable;
import org.gradle.api.Incubating;
import org.gradle.api.Nullable;
import org.gradle.api.Task;

/**
 * A model element that is directly buildable.
 * Such an element mirrors a specified lifecycle task in the DAG, and can accept dependencies which are then associated with the lifecycle task.
 */
@Incubating
public interface BuildableModelElement extends Buildable {
	/**
     * Returns the 'lifecycle' task associated with the construction of this element.
     */
    @Nullable
	Task getBuildTask();

    /**
     * Associates a 'lifecycle' task with the construction of this element.
     */
    void setBuildTask(Task lifecycleTask);

    /**
     * Adds a task that is required for the construction of this element.
     * A task added this way is then added as a dependency of the associated lifecycle task.
     */
    void builtBy(Object... tasks);

    boolean hasBuildDependencies();
}
