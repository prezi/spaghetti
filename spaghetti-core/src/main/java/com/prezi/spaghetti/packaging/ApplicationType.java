package com.prezi.spaghetti.packaging;

public enum ApplicationType {
	AMD("AMD", new AmdApplicationPackager()),
	COMMON_JS("Common JS", new CommonJsApplicationPackager()),
	SINGLE_FILE("single file", new SingleFileApplicationPackager());

	private final String description;
	private final ApplicationPackager packager;

	ApplicationType(String description, ApplicationPackager packager) {
		this.description = description;
		this.packager = packager;
	}

	public final String getDescription() {
		return description;
	}

	public final ApplicationPackager getPackager() {
		return packager;
	}
}
