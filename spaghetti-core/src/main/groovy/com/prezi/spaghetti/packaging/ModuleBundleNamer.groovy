package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

/**
 * Gives a name to a module bundle directory or file.
 */
public interface ModuleBundleNamer {
	String name(ModuleBundle bundle)

	public static final BUNDLE_NAME_NAMER = new ModuleBundleNamer() {
		@Override
		String name(ModuleBundle bundle) {
			bundle.name
		}
	}
}
