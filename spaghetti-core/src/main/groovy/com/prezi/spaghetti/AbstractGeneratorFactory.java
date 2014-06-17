package com.prezi.spaghetti;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractGeneratorFactory implements GeneratorFactory {
	private final String platform;
	private final String description;

	protected AbstractGeneratorFactory(String platform, String description) {
		this.platform = platform;
		this.description = description;
	}

	@Override
	public Set<String> getProtectedSymbols() {
		return Collections.emptySet();
	}

	public final String getPlatform() {
		return platform;
	}

	public final String getDescription() {
		return description;
	}

}
