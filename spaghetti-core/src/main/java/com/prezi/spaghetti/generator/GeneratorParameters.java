package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.definition.ModuleConfiguration;

public interface GeneratorParameters {
	ModuleConfiguration getModuleConfiguration();
	String getHeader();
}
