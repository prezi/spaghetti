package com.prezi.spaghetti.gradle.internal;

import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.IConventionAware;

public interface SpaghettiTask extends Task, IConventionAware {
	public ConfigurableFileCollection getDependentModules();
	public ConfigurableFileCollection getLazyDependentModules();
}
