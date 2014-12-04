package com.prezi.spaghetti.generator.internal;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters;

public class DefaultJavaScriptBundleProcessorParameters implements JavaScriptBundleProcessorParameters {
	private final ModuleConfiguration moduleConfiguration;

	public DefaultJavaScriptBundleProcessorParameters(ModuleConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return moduleConfiguration;
	}
}
