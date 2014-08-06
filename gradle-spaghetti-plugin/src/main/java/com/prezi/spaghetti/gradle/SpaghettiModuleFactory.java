package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.incubating.BinaryNamingScheme;

public interface SpaghettiModuleFactory<T> {
	SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, T payload);
}
