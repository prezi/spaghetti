package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

/**
 * Created by lptr on 25/05/14.
 */
class CommonJsApplicationPackager extends AbstractStructuredApplicationPackager {
	CommonJsApplicationPackager() {
		super(new CommonJsWrapper())
	}

	@Override
	String getModuleFileName(ModuleBundle bundle) {
		return "index.js"
	}
}
