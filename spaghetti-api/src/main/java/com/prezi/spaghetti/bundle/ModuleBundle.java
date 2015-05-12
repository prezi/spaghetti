package com.prezi.spaghetti.bundle;

import java.io.IOException;
import java.util.Set;

/**
 * Represents a module bundle.
 */
public interface ModuleBundle extends Comparable<ModuleBundle> {
	/**
	 * Returns the module's name.
	 */
	String getName();

	/**
	 * Returns the module's version.
	 */
	String getVersion();

	/**
	 * Returns the source URL of the module.
	 */
	String getSourceBaseUrl();

	/**
	 * Returns the names of the module's dependencies.
	 */
	Set<String> getDependentModules();

	/**
	 * Returns the list of resource file paths.
	 */
	Set<String> getResourcePaths();

	/**
	 * Returns the contents of the module's definition.
	 */
	String getDefinition() throws IOException;

	/**
	 * Returns the module's JavaScript code.
	 */
	String getJavaScript() throws IOException;

	/**
	 * Returns the module's source map, or <code>null</code> if there is no source map.
	 */
	String getSourceMap() throws IOException;
}
