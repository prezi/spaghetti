package com.prezi.spaghetti.bundle.internal;

import com.prezi.spaghetti.bundle.ModuleBundleFactory;

/**
 * Types of elements of a module bundle.
 * <p>
 * Used in {@link ModuleBundleFactory#extract(com.prezi.spaghetti.bundle.ModuleBundle, java.io.File, ModuleBundleElement...)}.
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
