package com.prezi.spaghetti.packaging.internal;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class AmdModulePackager extends AbstractModulePackager {
	public AmdModulePackager() {
		super(new AmdModuleWrapper());
	}

	@Override
	public String getModuleName(ModuleBundle bundle) {
		return bundle.getName() + ".js";
	}

}
