package com.prezi.spaghetti.gradle;

import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.runtime.base.internal.AbstractBuildableModelElement;
import org.gradle.runtime.base.internal.BinaryInternal;
import org.gradle.runtime.base.internal.BinaryNamingScheme;

import java.io.File;
import java.util.concurrent.Callable;

public abstract class AbstractSpaghettiModule extends AbstractBuildableModelElement implements BinaryInternal, SpaghettiModule {
	private final BinaryNamingScheme namingScheme;
	private final SpaghettiModuleData data;

	public AbstractSpaghettiModule(BinaryNamingScheme namingScheme, SpaghettiModuleData data) {
		this.namingScheme = namingScheme;
		this.data = data;
	}

	@Override
	public Callable<File> getJavaScriptFile() {
		return data.getJavaScriptFile();
	}

	@Override
	public Callable<File> getSourceMapFile() {
		return data.getSourceMapFile();
	}

	@Override
	public BundleModule getBundleTask() {
		return data.getBundleTask();
	}

	@Override
	public ObfuscateModule getObfuscateTask() {
		return data.getObfuscateTask();
	}

	@Override
	public AbstractArchiveTask getArchiveTask() {
		return data.getArchiveTask();
	}

	@Override
	public AbstractArchiveTask getArchiveObfuscatedTask() {
		return data.getArchiveObfuscatedTask();
	}

	@Override
	public BinaryNamingScheme getNamingScheme() {
		return namingScheme;
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
		return getName() + " Spaghetti module binary";
	}
}
