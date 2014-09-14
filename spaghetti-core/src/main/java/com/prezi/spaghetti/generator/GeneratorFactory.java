package com.prezi.spaghetti.generator;

import java.util.Set;

/**
 * Creates generators for a language.
 */
public interface GeneratorFactory {
	/**
	 * Returns name of supported language.
	 *
	 * @return the identifier of the language.
	 */
	public abstract String getLanguage();

	/**
	 * Returns description of the language.
	 *
	 * @return the description of the language.
	 */
	public abstract String getDescription();

	/**
	 * Creates generator.
	 *
	 * @param params The generation parameters.
	 * @return the generator.
	 */
	public abstract Generator createGenerator(GeneratorParameters params);

	/**
	 * Returns set of symbols that need to be protected by the obfuscator.
	 *
	 * @return a set of symbols.
	 */
	public abstract Set<String> getProtectedSymbols();
}
