package com.prezi.spaghetti.packaging.internal;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class AmdApplicationPackager extends AbstractStructuredApplicationPackager {
	public AmdApplicationPackager() {
		super(new AmdModuleWrapper());
	}

	@Override
	protected String getModuleFileName(ModuleBundle bundle) {
		return bundle.getName() + ".js";
	}

}
