package com.prezi.spaghetti.kotlin.gradle.internal;

import com.prezi.spaghetti.gradle.internal.AbstractSpaghettiModule;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;

public class KotlinSpaghettiModule extends AbstractSpaghettiModule {
	private final boolean usedForTesting;

	public KotlinSpaghettiModule(BinaryNamingScheme namingScheme, SpaghettiModuleData data, boolean usedForTesting) {
		super(namingScheme, data);
		this.usedForTesting = usedForTesting;
	}

	@Override
	public boolean isUsedForTesting() {
		return usedForTesting;
	}
}
