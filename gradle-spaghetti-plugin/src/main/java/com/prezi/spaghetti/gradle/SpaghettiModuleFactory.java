package com.prezi.spaghetti.gradle;

import org.gradle.runtime.base.internal.BinaryNamingScheme;

public interface SpaghettiModuleFactory<T> {
	SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, T payload);
}
