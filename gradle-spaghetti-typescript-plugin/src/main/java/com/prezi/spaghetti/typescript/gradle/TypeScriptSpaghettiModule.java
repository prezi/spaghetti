package com.prezi.spaghetti.typescript.gradle;

import com.prezi.spaghetti.gradle.AbstractSpaghettiModule;
import com.prezi.spaghetti.gradle.SpaghettiModuleData;
import com.prezi.typescript.gradle.TypeScriptBinaryBase;
import org.gradle.runtime.base.internal.BinaryNamingScheme;

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
