package com.prezi.spaghetti.typescript.gradle.internal;

import com.prezi.spaghetti.gradle.internal.AbstractSpaghettiModule;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;
import com.prezi.typescript.gradle.TypeScriptBinaryBase;

public class TypeScriptSpaghettiModule extends AbstractSpaghettiModule {
	private final TypeScriptBinaryBase original;
	private final boolean usedForTesting;

	public TypeScriptSpaghettiModule(BinaryNamingScheme namingScheme, SpaghettiModuleData data, TypeScriptBinaryBase original, boolean usedForTesting) {
		super(namingScheme, data);
		this.original = original;
		this.usedForTesting = usedForTesting;
	}

	public TypeScriptBinaryBase getOriginal() {
		return original;
	}

	@Override
	public boolean isUsedForTesting() {
		return usedForTesting;
	}
}
