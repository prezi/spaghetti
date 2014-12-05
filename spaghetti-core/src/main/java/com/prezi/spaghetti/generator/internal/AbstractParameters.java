package com.prezi.spaghetti.generator.internal;

import com.google.common.collect.ImmutableSortedMap;
import com.prezi.spaghetti.generator.ParametersBase;

import java.util.Map;

public class AbstractParameters implements ParametersBase {
	private final Map<String, String> options;

	public AbstractParameters(Map<String, String> options) {
		this.options = ImmutableSortedMap.copyOf(options);
	}

	@Override
	public boolean hasOption(String name) {
		return options.containsKey(name);
	}

	@Override
	public String getOption(String name) {
		return options.get(name);
	}

	@Override
	public String getOption(String name, String defaultValue) {
		String value = options.get(name);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public Map<String, String> getOptions() {
		return options;
	}
}
