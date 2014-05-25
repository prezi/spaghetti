package com.prezi.spaghetti.packaging
/**
 * Created by lptr on 23/05/14.
 */
enum ApplicationType {
	AMD("AMD", new AmdApplicationPackager()),
	COMMON_JS("Common JS", new CommonJsApplicationPackager()),
	SINGLE_FILE("single file", new SingleFileApplicationPackager())

	final String description
	final ApplicationPackager packager

	ApplicationType(String description, ApplicationPackager packager) {
		this.description = description
		this.packager = packager
	}
}
