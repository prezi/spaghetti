package com.prezi.spaghetti.gradle.internal;

import org.gradle.api.Task;
import org.gradle.api.internal.IConventionAware;

import com.prezi.spaghetti.definition.DefinitionFile;

public interface DefinitionAwareSpaghettiTask extends Task, IConventionAware {
	DefinitionFile getDefinition();
	void setDefinition(DefinitionFile definition);
}
