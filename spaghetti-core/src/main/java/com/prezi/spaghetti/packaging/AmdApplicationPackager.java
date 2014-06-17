package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

public class AmdApplicationPackager extends AbstractStructuredApplicationPackager {
	public AmdApplicationPackager() {
		super(new AmdWrapper());
	}

	@Override
	protected String getModuleFileName(ModuleBundle bundle) {
		return bundle.getName() + ".js";
	}

}
