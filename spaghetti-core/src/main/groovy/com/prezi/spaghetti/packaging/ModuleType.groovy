package com.prezi.spaghetti.packaging

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
