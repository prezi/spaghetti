package com.prezi.spaghetti.bundle.internal;

import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.structure.internal.StructuredAppender;

import java.io.IOException;
import java.util.EnumSet;

public interface ModuleBundleInternal extends ModuleBundle {
	void extract(StructuredAppender output, EnumSet<ModuleBundleElement> elements) throws IOException;
}
