package com.prezi.spaghetti.haxe.gradle;

import com.prezi.haxe.gradle.HaxeBinaryBase;
import com.prezi.spaghetti.gradle.AbstractSpaghettiModule;
import com.prezi.spaghetti.gradle.SpaghettiModuleData;
import org.gradle.runtime.base.internal.BinaryNamingScheme;

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

	public boolean isUsedForTesting() {
		return usedForTesting;
	}
}
