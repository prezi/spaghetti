package com.prezi.spaghetti.generator.internal;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.GeneratorParameters;

public class DefaultGeneratorParameters implements GeneratorParameters {
	private final ModuleConfiguration moduleConfiguration;
	private final String headerComment;

	public DefaultGeneratorParameters(ModuleConfiguration moduleConfiguration, String headerComment) {
		this.moduleConfiguration = moduleConfiguration;
		this.headerComment = headerComment;
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return moduleConfiguration;
	}

	@Override
	public String getHeader() {
		return headerComment;
	}
}
