package com.prezi.spaghetti.packaging.internal;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class CommonJsModulePackager extends AbstractModulePackager {
	public CommonJsModulePackager() {
		super(new UmdModuleWrapper());
	}

	@Override
	public String getModuleName(ModuleBundle bundle) {
		return "index.js";
	}

}
