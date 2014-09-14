package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;

public interface SpaghettiModuleFactory<T> {
	SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, T payload);
}
