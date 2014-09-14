package com.prezi.spaghetti.haxe.gradle.internal;

import com.prezi.haxe.gradle.HaxeBinaryBase;
import com.prezi.spaghetti.gradle.internal.AbstractSpaghettiModule;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;

public class HaxeSpaghettiModule extends AbstractSpaghettiModule {
	private final HaxeBinaryBase<?> original;
	private final boolean usedForTesting;

	public HaxeSpaghettiModule(BinaryNamingScheme namingScheme, SpaghettiModuleData data, HaxeBinaryBase<?> original, boolean usedForTesting) {
		super(namingScheme, data);
		this.original = original;
		this.usedForTesting = usedForTesting;
	}

	public HaxeBinaryBase<?> getOriginal() {
		return original;
	}

	@Override
	public boolean isUsedForTesting() {
		return usedForTesting;
	}
}
