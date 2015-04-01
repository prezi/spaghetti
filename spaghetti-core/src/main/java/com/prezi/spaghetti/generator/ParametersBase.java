package com.prezi.spaghetti.generator;

import java.util.Map;

public interface ParametersBase {
	boolean hasOption(String name);
	String getOption(String name);
	String getOption(String name, String defaultValue);
	Map<String, String> getOptions();
}
