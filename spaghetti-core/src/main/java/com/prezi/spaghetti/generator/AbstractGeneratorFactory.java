package com.prezi.spaghetti.generator;

import java.util.Collections;
import java.util.Set;

/**
 * Abstract implementation of {@link GeneratorFactory}. Derive from this class instead of
 * implementing {@link GeneratorFactory} directly for future compatibility.
 */
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

	@Override
	public final String getLanguage() {
		return language;
	}

	@Override
	public final String getDescription() {
		return description;
	}
}
