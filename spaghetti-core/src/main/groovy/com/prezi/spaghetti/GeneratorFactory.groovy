package com.prezi.spaghetti

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
	 * Maps the platform's own representations to native names.
	 */
	Map<String, String> getExternMapping()

	/**
	 * Returns set of symbols that need to be protected by the obfuscator.
	 */
	Set<String> getProtectedSymbols()
}
