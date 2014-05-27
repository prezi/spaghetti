package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

/**
 * Created by lptr on 25/05/14.
 */
class AmdApplicationPackager extends AbstractStructuredApplicationPackager {
	AmdApplicationPackager() {
		super(new AmdWrapper())
	}

	@Override
	protected String getModuleFileName(ModuleBundle bundle) {
		return bundle.name + ".js"
	}
}
