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

	public static ApplicationType fromString(String typeName) {
		String typeUC = typeName.toUpperCase();
		ApplicationType type;
		if (typeUC.equals("AMD")
				|| typeUC.equals("REQUIRE_JS")
				|| typeUC.equals("REQUIREJS")) {
			type = ApplicationType.AMD;
		} else if (typeUC.equals("COMMON_JS")
				|| typeUC.equals("COMMONJS")
				|| typeUC.equals("NODE_JS")
				|| typeUC.equals("NODEJS")
				|| typeUC.equals("NODE")) {
			type = ApplicationType.COMMON_JS;
		} else if (typeUC.equals("SINGLE_FILE")
				|| typeUC.equals("SINGLEFILE")
				|| typeUC.equals("SINGLE")) {
			type = ApplicationType.SINGLE_FILE;
		} else {
			throw new IllegalArgumentException("Unknown module type: " + typeName);
		}
		return type;
	}
}
