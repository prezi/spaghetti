package com.prezi.spaghetti

abstract class AbstractGeneratorFactory implements GeneratorFactory {
	final String platform
	final String description

	protected AbstractGeneratorFactory(String platform, String description) {
		this.platform = platform
		this.description = description
	}

	@Override
	Set<String> getProtectedSymbols() {
		Collections.emptySet()
	}
}
