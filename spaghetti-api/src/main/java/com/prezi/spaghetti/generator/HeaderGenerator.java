package com.prezi.spaghetti.generator;

import java.io.File;
import java.io.IOException;

/**
 * Generates the interface in a given language.
 */
public interface HeaderGenerator extends GeneratorService {
	/**
	 * Returns the supported language of the generator.
	 * @return the supported language of the generator.
	 */
	@Override
	String getLanguage();

	/**
	 * Generate headers.
	 *
	 * @param outputDirectory the directory to generate header files to.
	 */
	void generateHeaders(GeneratorParameters params, File outputDirectory) throws IOException;
}
