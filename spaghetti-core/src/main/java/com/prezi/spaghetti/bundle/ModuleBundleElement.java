package com.prezi.spaghetti.bundle;

/**
 * Types of elements of a module bundle.
 * <p>
 * Used in {@link com.prezi.spaghetti.bundle.ModuleBundleFactory#extract(ModuleBundle, java.io.File, ModuleBundleElement...)}.
 * </p>
 */
public enum ModuleBundleElement {
	/**
	 * The JavaScript code of the module.
	 */
	JAVASCRIPT,

	/**
	 * The definition of the module.
	 */
	DEFINITION,

	/**
	 * The metadata file of the module.
	 */
	MANIFEST,

	/**
	 * The source map of the module.
	 */
	SOURCE_MAP,

	/**
	 * The resources of the module.
	 */
	RESOURCES
}
