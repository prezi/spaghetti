package com.prezi.spaghetti;

import com.prezi.spaghetti.config.ModuleConfiguration;

import java.util.Set;

public interface GeneratorFactory {
	/**
	 * Returns name of supported platform.
	 *
	 * @return the identifier of the platform.
	 */
	public abstract String getPlatform();

	/**
	 * Returns description of the platform.
	 *
	 * @return the description of the platform.
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
