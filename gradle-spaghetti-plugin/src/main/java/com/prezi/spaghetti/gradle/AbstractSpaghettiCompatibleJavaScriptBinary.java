package com.prezi.spaghetti.gradle;

import org.gradle.runtime.base.internal.AbstractBuildableModelElement;
import org.gradle.runtime.base.internal.BinaryInternal;
import org.gradle.runtime.base.internal.BinaryNamingScheme;

import java.io.File;

public abstract class AbstractSpaghettiCompatibleJavaScriptBinary extends AbstractBuildableModelElement implements SpaghettiCompatibleJavaScriptBinary, BinaryInternal {
	private final BinaryNamingScheme namingScheme;
	private final boolean usedForTesting;
	private BundleModule bundleTask;
	private ObfuscateModule obfuscateTask;

	public AbstractSpaghettiCompatibleJavaScriptBinary(String name, boolean testing) {
		this.namingScheme = new SpaghettiCompatibleBinaryNamingScheme(name);
		this.usedForTesting = testing;
	}

	public BundleModule getBundleTask() {
		return bundleTask;
	}

	public void setBundleTask(BundleModule bundleTask) {
		this.bundleTask = bundleTask;
	}

	public ObfuscateModule getObfuscateTask() {
		return obfuscateTask;
	}

	public void setObfuscateTask(ObfuscateModule obfuscateTask) {
		this.obfuscateTask = obfuscateTask;
	}

	public boolean isUsedForTesting() {
		return usedForTesting;
	}

	@Override
	public BinaryNamingScheme getNamingScheme() {
		return namingScheme;
	}

	@Override
	public File getSourceMapFile() {
		File outputFile = getJavaScriptFile();
		File sourceMapFile = new File(outputFile.getParentFile(), outputFile.getName() + ".map");
		return sourceMapFile.exists() ? sourceMapFile : null;
	}

	@Override
	public String getDisplayName() {
		return namingScheme.getDescription();
	}

	@Override
	public String getName() {
		return namingScheme.getLifecycleTaskName();
	}

	@Override
	public String toString() {
		return getName() + " Spaghetti binary";
	}
}
