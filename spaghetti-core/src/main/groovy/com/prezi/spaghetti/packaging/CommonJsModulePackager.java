package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class CommonJsModulePackager extends AbstractModulePackager {
	public CommonJsModulePackager() {
		super(new CommonJsWrapper());
	}

	@Override
	public String getModuleName(ModuleBundle bundle) {
		return "index.js";
	}

}
