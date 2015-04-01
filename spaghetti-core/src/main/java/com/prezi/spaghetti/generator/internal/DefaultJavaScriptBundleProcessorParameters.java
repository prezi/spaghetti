package com.prezi.spaghetti.generator.internal;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters;

import java.util.Map;

public class DefaultJavaScriptBundleProcessorParameters extends AbstractParameters implements JavaScriptBundleProcessorParameters {
	private final ModuleConfiguration moduleConfiguration;

	public DefaultJavaScriptBundleProcessorParameters(ModuleConfiguration moduleConfiguration, Map<String, String> options) {
		super(options);
		this.moduleConfiguration = moduleConfiguration;
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return moduleConfiguration;
	}
}
