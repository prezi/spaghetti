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
		if (typeUC.equals("AMD") || typeUC.equals("REQUIREJS")) {
			type = ApplicationType.AMD;
		} else if (typeUC.equals("COMMONJS") || typeUC.equals("NODE") || typeUC.equals("NODEJS")) {
			type = ApplicationType.COMMON_JS;
		} else if (typeUC.equals("SINGLE") || typeUC.equals("SINGLEFILE")) {
			type = ApplicationType.SINGLE_FILE;
		} else {
			throw new IllegalArgumentException("Unknown module type: " + typeName);
		}
		return type;
	}
}
