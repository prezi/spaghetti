package com.prezi.spaghetti.generator;

abstract public class AbstractStubGenerator extends AbstractGeneratorService implements StubGenerator {
	protected AbstractStubGenerator(String language) {
		super(language);
	}
}
