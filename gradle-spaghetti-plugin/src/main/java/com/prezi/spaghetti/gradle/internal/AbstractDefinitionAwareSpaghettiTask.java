package com.prezi.spaghetti.gradle.internal;

import com.google.common.collect.Maps;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;

import java.io.File;
import java.util.Map;

public class AbstractDefinitionAwareSpaghettiTask extends AbstractLanguageAwareSpaghettiTask {
	private File definition;
	private Map<String, String> options = Maps.newLinkedHashMap();

	@InputFile
	public File getDefinition() {
		return definition;
	}

	public void setDefinition(Object definition) {
		this.definition = getProject().file(definition);
	}

	public void definition(Object definition) {
		setDefinition(definition);
	}

	@Input
	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	public void options(Map<String, String> options) {
		setOptions(options);
	}

	public void option(String key, String value) {
		getOptions().put(key, value);
	}

	public void option(String key) {
		option(key, "true");
	}
}
