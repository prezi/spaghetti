package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

/**
 * Created by lptr on 27/05/14.
 */
class CommonJsModulePackager extends AbstractModulePackager {
	CommonJsModulePackager() {
		super(new CommonJsWrapper())
	}

	@Override
	String getModuleName(ModuleBundle bundle) {
		return "index.js"
	}
}
