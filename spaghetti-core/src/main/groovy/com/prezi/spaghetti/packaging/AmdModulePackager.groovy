package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

/**
 * Created by lptr on 27/05/14.
 */
class AmdModulePackager extends AbstractModulePackager {
	AmdModulePackager() {
		super(new AmdWrapper())
	}

	@Override
	String getModuleName(ModuleBundle bundle) {
		return bundle.name + ".js"
	}
}
