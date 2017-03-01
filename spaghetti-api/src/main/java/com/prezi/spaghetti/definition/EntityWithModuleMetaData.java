package com.prezi.spaghetti.definition;

import com.prezi.spaghetti.bundle.ModuleFormat;

public interface EntityWithModuleMetaData<T> {
	T getEntity();

	ModuleFormat getFormat();
}
