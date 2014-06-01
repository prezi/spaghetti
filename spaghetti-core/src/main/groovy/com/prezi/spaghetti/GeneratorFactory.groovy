package com.prezi.spaghetti

import com.prezi.spaghetti.config.ModuleConfiguration

/**
 * Created by lptr on 23/11/13.
 */
public interface GeneratorFactory {
	/**
	 * Returns name of supported platform.
	 */
	String getPlatform()

	/**
	 * Returns description of generator.
	 */
	String getDescription()

	/**
	 * Creates generator.
	 */
	Generator createGenerator(ModuleConfiguration configuration)

	/**
	 * Returns set of symbols that need to be protected by the obfuscator.
	 */
	Set<String> getProtectedSymbols()
}
