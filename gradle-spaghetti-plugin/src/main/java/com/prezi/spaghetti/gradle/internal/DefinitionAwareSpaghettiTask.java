package com.prezi.spaghetti.gradle.internal;

import org.gradle.api.Task;
import org.gradle.api.internal.IConventionAware;

import java.io.File;

public interface DefinitionAwareSpaghettiTask extends Task, IConventionAware {
	File getDefinition();
	void setDefinition(Object definition);
}
