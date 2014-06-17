package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

class AmdModulePackager extends AbstractModulePackager {
	AmdModulePackager() {
		super(new AmdWrapper())
	}

	@Override
	String getModuleName(ModuleBundle bundle) {
		return bundle.name + ".js"
	}
}
