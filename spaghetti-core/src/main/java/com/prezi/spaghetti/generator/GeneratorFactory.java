package com.prezi.spaghetti.generator;

import com.prezi.spaghetti.config.ModuleConfiguration;

import java.util.Set;

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
	 * @param configuration the module configuration the generator will use.
	 * @return the generator.
	 */
	public abstract Generator createGenerator(ModuleConfiguration configuration);

	/**
	 * Returns set of symbols that need to be protected by the obfuscator.
	 *
	 * @return a set of symbols.
	 */
	public abstract Set<String> getProtectedSymbols();
}
