package com.prezi.spaghetti.generator.internal;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.GeneratorParameters;

import java.util.Map;

public class DefaultGeneratorParameters extends AbstractParameters implements GeneratorParameters {
	private final ModuleConfiguration moduleConfiguration;
	private final String headerComment;

	public DefaultGeneratorParameters(ModuleConfiguration moduleConfiguration, String headerComment, Map<String, String> options) {
		super(options);
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
