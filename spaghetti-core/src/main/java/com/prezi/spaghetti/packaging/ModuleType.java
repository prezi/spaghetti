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

	public static ModuleType fromString(String typeName) {
		String typeUC = typeName.toUpperCase();
		ModuleType type;
		if (typeUC.equals("AMD")
				|| typeUC.equals("REQUIRE_JS")
				|| typeUC.equals("REQUIREJS")) {
			type = ModuleType.AMD;
		} else if (typeUC.equals("COMMON_JS")
				|| typeUC.equals("COMMONJS")
				|| typeUC.equals("NODE_JS")
				|| typeUC.equals("NODEJS")
				|| typeUC.equals("NODE")) {
			type = ModuleType.COMMON_JS;
		} else {
			throw new IllegalArgumentException("Unknown module type: " + typeName);
		}
		return type;
	}
}
