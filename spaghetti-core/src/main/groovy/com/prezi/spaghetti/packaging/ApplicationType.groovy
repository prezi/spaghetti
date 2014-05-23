package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

import static ModuleBundleNamer.BUNDLE_NAME_NAMER

/**
 * Created by lptr on 23/05/14.
 */
enum ApplicationType {
	AMD(new AmdWrapper(), BUNDLE_NAME_NAMER, BUNDLE_NAME_NAMER),
	COMMON_JS(new CommonJsWrapper(), BUNDLE_NAME_NAMER, new ModuleBundleNamer() {
		@Override
		String name(ModuleBundle bundle) {
			"index"
		}
	})

	final Wrapper wrapper
	final ModuleBundleNamer moduleDirectoryNamer
	final ModuleBundleNamer moduleFileNamer

	ApplicationType(Wrapper wrapper, ModuleBundleNamer directoryNamer, ModuleBundleNamer fileNamer) {
		this.wrapper = wrapper
		this.moduleDirectoryNamer = directoryNamer
		this.moduleFileNamer = fileNamer
	}
}
