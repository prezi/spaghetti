package com.prezi.spaghetti.gradle;

import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.runtime.base.internal.AbstractBuildableModelElement;
import org.gradle.runtime.base.internal.BinaryInternal;
import org.gradle.runtime.base.internal.BinaryNamingScheme;

import java.io.File;

public abstract class AbstractSpaghettiCompatibleJavaScriptBinary extends AbstractBuildableModelElement implements SpaghettiCompatibleJavaScriptBinary, BinaryInternal {
	private final BinaryNamingScheme namingScheme;
	private final boolean usedForTesting;
	private BundleModule bundleTask;
	private ObfuscateModule obfuscateTask;
	private AbstractArchiveTask archiveTask;
	private AbstractArchiveTask archiveObfuscatedTask;

	public AbstractSpaghettiCompatibleJavaScriptBinary(String name, boolean testing) {
		this.namingScheme = new SpaghettiCompatibleBinaryNamingScheme(name);
		this.usedForTesting = testing;
	}

	@Override
	public BundleModule getBundleTask() {
		return bundleTask;
	}

	@Override
	public void setBundleTask(BundleModule bundleTask) {
		this.bundleTask = bundleTask;
	}

	@Override
	public ObfuscateModule getObfuscateTask() {
		return obfuscateTask;
	}

	@Override
	public void setObfuscateTask(ObfuscateModule obfuscateTask) {
		this.obfuscateTask = obfuscateTask;
	}

	@Override
	public AbstractArchiveTask getArchiveTask() {
		return archiveTask;
	}

	@Override
	public void setArchiveTask(AbstractArchiveTask archiveTask) {
		this.archiveTask = archiveTask;
	}

	@Override
	public AbstractArchiveTask getArchiveObfuscatedTask() {
		return archiveObfuscatedTask;
	}

	@Override
	public void setArchiveObfuscatedTask(AbstractArchiveTask archiveObfuscatedTask) {
		this.archiveObfuscatedTask = archiveObfuscatedTask;
	}

	@Override
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
