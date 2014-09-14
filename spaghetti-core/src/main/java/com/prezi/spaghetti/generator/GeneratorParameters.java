package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.config.ModuleConfiguration;

public interface GeneratorParameters {
	ModuleConfiguration getModuleConfiguration();
	String getHeader();
}
