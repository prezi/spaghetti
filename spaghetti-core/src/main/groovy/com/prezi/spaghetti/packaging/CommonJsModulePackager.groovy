package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

class CommonJsModulePackager extends AbstractModulePackager {
	CommonJsModulePackager() {
		super(new CommonJsWrapper())
	}

	@Override
	String getModuleName(ModuleBundle bundle) {
		return "index.js"
	}
}
