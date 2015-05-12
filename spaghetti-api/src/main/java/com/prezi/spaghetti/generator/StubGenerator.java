package com.prezi.spaghetti.generator;

import java.io.File;
import java.io.IOException;

/**
 * Generates empty implementations for a Spaghetti API in a certain language.
 */
public interface StubGenerator extends GeneratorService {
	/**
	 * Returns the supported language of the generator.
	 * @return the supported language of the generator.
	 */
	@Override
	String getLanguage();

	/**
	 * Generate stubs.
	 *
	 * @param outputDirectory the directory to generate stub files to.
	 */
	void generateStubs(GeneratorParameters params, File outputDirectory) throws IOException;
}
