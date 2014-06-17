package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class AmdModulePackager extends AbstractModulePackager {
	public AmdModulePackager() {
		super(new AmdWrapper());
	}

	@Override
	public String getModuleName(ModuleBundle bundle) {
		return bundle.getName() + ".js";
	}

}
