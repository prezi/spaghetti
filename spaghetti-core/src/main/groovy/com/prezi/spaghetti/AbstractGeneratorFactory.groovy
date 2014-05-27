package com.prezi.spaghetti

/**
 * Created by lptr on 16/05/14.
 */
abstract class AbstractGeneratorFactory implements GeneratorFactory {
	final String platform
	final String description

	protected AbstractGeneratorFactory(String platform, String description) {
		this.platform = platform
		this.description = description
	}

	@Override
	Map<String, String> getExternMapping() {
		Collections.emptyMap()
	}

	@Override
	Set<String> getProtectedSymbols() {
		Collections.emptySet()
	}
}
