package com.prezi.spaghetti.packaging.internal;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class CommonJsApplicationPackager extends AbstractStructuredApplicationPackager {
	public CommonJsApplicationPackager() {
		super(new UmdModuleWrapper());
	}

	@Override
	public String getModuleFileName(ModuleBundle bundle) {
		return "index.js";
	}

	@Override
	public String getModulesDirectory() {
		return "node_modules";
	}

}
