package com.prezi.spaghetti.packaging.internal;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class CommonJsApplicationPackager extends AbstractStructuredApplicationPackager {
	public CommonJsApplicationPackager() {
		super(new CommonJsModuleWrapper());
	}

	@Override
	public String getModuleFileName(ModuleBundle bundle) {
		return "index.js";
	}

}
