package com.prezi.spaghetti.packaging

/**
 * Created by lptr on 27/05/14.
 */
enum ModuleType {
	AMD("AMD", new AmdModulePackager()),
	COMMON_JS("Common JS", new CommonJsModulePackager())

	final String description
	final ModulePackager packager

	ModuleType(String description, ModulePackager packager) {
		this.description = description
		this.packager = packager
	}
}
