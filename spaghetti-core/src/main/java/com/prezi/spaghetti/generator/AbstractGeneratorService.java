package com.prezi.spaghetti.generator;

abstract public class AbstractGeneratorService implements GeneratorService {

	private final String language;

	protected AbstractGeneratorService(String language) {
		this.language = language;
	}

	@Override
	public String getLanguage() {
		return language;
	}
}
