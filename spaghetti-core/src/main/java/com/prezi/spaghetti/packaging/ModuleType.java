package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.packaging.internal.AmdModulePackager;
import com.prezi.spaghetti.packaging.internal.CommonJsModulePackager;

public enum ModuleType {
	/**
	 * AMD module type, used for RequireJS applications.
	 */
	AMD("AMD", new AmdModulePackager()),

	/**
	 * CommonJS module type, used for NodeJS applications.
	 */
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
