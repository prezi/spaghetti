package com.prezi.spaghetti.packaging;

public enum ModuleType {
	AMD("AMD", new AmdModulePackager()),
	COMMON_JS("Common JS", new CommonJsModulePackager());

	private final String description;
	private final ModulePackager packager;

	ModuleType(String description, ModulePackager packager) {
		this.description = description;
		this.packager = packager;
	}

	public final String getDescription() {
		return description;
	}

	public final ModulePackager getPackager() {
		return packager;
	}
}
