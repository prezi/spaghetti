package com.prezi.spaghetti.generator;

abstract public class AbstractHeaderGenerator extends AbstractGeneratorService implements HeaderGenerator {
	protected AbstractHeaderGenerator(String language) {
		super(language);
	}
}
