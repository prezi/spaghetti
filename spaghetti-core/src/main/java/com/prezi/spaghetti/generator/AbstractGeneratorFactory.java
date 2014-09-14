package com.prezi.spaghetti.generator;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractGeneratorFactory implements GeneratorFactory {
	private final String language;
	private final String description;

	protected AbstractGeneratorFactory(String language, String description) {
		this.language = language;
		this.description = description;
	}

	@Override
	public Set<String> getProtectedSymbols() {
		return Collections.emptySet();
	}

	public final String getLanguage() {
		return language;
	}

	public final String getDescription() {
		return description;
	}

}
