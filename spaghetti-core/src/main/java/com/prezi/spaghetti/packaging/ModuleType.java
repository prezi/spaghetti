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
		if (typeUC.equals("AMD") || typeUC.equals("REQUIREJS")) {
			type = ModuleType.AMD;
		} else if (typeUC.equals("COMMONJS") || typeUC.equals("NODE") || typeUC.equals("NODEJS")) {
			type = ModuleType.COMMON_JS;
		} else {
			throw new IllegalArgumentException("Unknown module type: " + typeName);
		}
		return type;
	}
}
