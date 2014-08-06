package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.Task;
import org.gradle.api.internal.tasks.DefaultTaskDependency;
import org.gradle.api.tasks.TaskDependency;

import java.util.Collections;
import java.util.Set;

public class AbstractBuildableModelElement implements BuildableModelElement {
	private final DefaultTaskDependency buildDependencies = new DefaultTaskDependency();
    private Task lifecycleTask;

    public Task getBuildTask() {
        return lifecycleTask;
    }

    public void setBuildTask(Task lifecycleTask) {
        this.lifecycleTask = lifecycleTask;
        lifecycleTask.dependsOn(buildDependencies);
    }

    public TaskDependency getBuildDependencies() {
        return new TaskDependency() {
            public Set<? extends Task> getDependencies(Task other) {
                if (lifecycleTask == null) {
                    return buildDependencies.getDependencies(other);
                }
                return Collections.singleton(lifecycleTask);
            }
        };
    }

    public void builtBy(Object... tasks) {
        buildDependencies.add(tasks);
    }

    public boolean hasBuildDependencies() {
        return buildDependencies.getDependencies(lifecycleTask).size() > 0;
    }
}
