package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.Binary;

public interface SpaghettiModule extends SpaghettiModuleData, Binary {
	boolean isUsedForTesting();
}
