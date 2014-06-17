package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class CommonJsApplicationPackager extends AbstractStructuredApplicationPackager {
	public CommonJsApplicationPackager() {
		super(new CommonJsWrapper());
	}

	@Override
	public String getModuleFileName(ModuleBundle bundle) {
		return "index.js";
	}

}
