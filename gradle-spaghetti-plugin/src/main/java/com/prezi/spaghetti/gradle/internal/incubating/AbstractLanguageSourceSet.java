package com.prezi.spaghetti.gradle.internal.incubating;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.SourceDirectorySet;

public abstract class AbstractLanguageSourceSet extends AbstractBuildableModelElement implements LanguageSourceSetInternal {
	private final String name;
    private final String fullName;
    private final String displayName;
    private final SourceDirectorySet source;
    private boolean generated;
    private Task generatorTask;

    public AbstractLanguageSourceSet(String name, FunctionalSourceSet parent, String typeName, SourceDirectorySet source) {
        this.name = name;
        this.fullName = parent.getName() + StringUtils.capitalize(name);
        this.displayName = String.format("%s '%s:%s'", typeName, parent.getName(), name);
        this.source = source;
        super.builtBy(source.getBuildDependencies());
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public void builtBy(Object... tasks) {
        generated = true;
        super.builtBy(tasks);
    }

    public void generatedBy(Task generatorTask) {
        this.generatorTask = generatorTask;
    }

    public Task getGeneratorTask() {
        return generatorTask;
    }

    public boolean getMayHaveSources() {
        // TODO:DAZ This doesn't take into account build dependencies of the SourceDirectorySet.
        // Should just ditch SourceDirectorySet from here since it's not really a great model, and drags in too much baggage.
        return generated || !source.isEmpty();
    }

    @Override
    public String toString() {
        return displayName;
    }

    public void source(Action<? super SourceDirectorySet> config) {
        config.execute(getSource());
    }

    public SourceDirectorySet getSource() {
        return source;
    }
}
