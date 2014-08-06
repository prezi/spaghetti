package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.incubating.Binary;

public interface SpaghettiModule extends SpaghettiModuleData, Binary {
	boolean isUsedForTesting();
}
