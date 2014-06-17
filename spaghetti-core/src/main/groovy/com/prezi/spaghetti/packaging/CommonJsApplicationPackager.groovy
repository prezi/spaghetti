package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

class CommonJsApplicationPackager extends AbstractStructuredApplicationPackager {
	CommonJsApplicationPackager() {
		super(new CommonJsWrapper())
	}

	@Override
	String getModuleFileName(ModuleBundle bundle) {
		return "index.js"
	}
}
