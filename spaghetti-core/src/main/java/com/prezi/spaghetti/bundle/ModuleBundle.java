package com.prezi.spaghetti.bundle;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

/**
 * Represents a module bundle.
 */
public interface ModuleBundle extends Comparable<ModuleBundle> {
	/**
	 * Path of the module definition inside the bundle.
	 */
	public static final String DEFINITION_PATH = "module.def";

	/**
	 * Path of the source map inside the bundle.
	 */
	public static final String SOURCE_MAP_PATH = "module.map";

	/**
	 * Path of the module's JavaScript code inside the bundle.
	 */
	public static final String JAVASCRIPT_PATH = "module.js";

	/**
	 * Path of the metadata file inside the bundle.
	 */
	public static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF";

	/**
	 * Path of the resources directory inside the bundle.
	 */
	public static final String RESOURCES_PREFIX = "resources/";

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
	 * Returns the list of external dependencies.
	 */
	SortedSet<String> getExternalDependencies();

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
