package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.BundleModule;
import com.prezi.spaghetti.gradle.ObfuscateModule;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;
import java.util.concurrent.Callable;

public class DefaultSpaghettiModuleData implements SpaghettiModuleData {
	private final Callable<File> javaScriptFile;
	private final Callable<File> sourceMapFile;

	private final BundleModule bundleTask;
	private final ObfuscateModule obfuscateTask;
	private final AbstractArchiveTask archiveTask;
	private final AbstractArchiveTask archiveObfuscatedTask;

	public DefaultSpaghettiModuleData(Callable<File> javaScriptFile, Callable<File> sourceMapFile, BundleModule bundleTask, ObfuscateModule obfuscateTask, AbstractArchiveTask archiveTask, AbstractArchiveTask archiveObfuscatedTask) {
		this.javaScriptFile = javaScriptFile;
		this.sourceMapFile = sourceMapFile;
		this.bundleTask = bundleTask;
		this.obfuscateTask = obfuscateTask;
		this.archiveTask = archiveTask;
		this.archiveObfuscatedTask = archiveObfuscatedTask;
	}

	@Override
	public Callable<File> getJavaScriptFile() {
		return javaScriptFile;
	}

	@Override
	public Callable<File> getSourceMapFile() {
		return sourceMapFile;
	}

	@Override
	public BundleModule getBundleTask() {
		return bundleTask;
	}

	@Override
	public ObfuscateModule getObfuscateTask() {
		return obfuscateTask;
	}

	@Override
	public AbstractArchiveTask getArchiveTask() {
		return archiveTask;
	}

	@Override
	public AbstractArchiveTask getArchiveObfuscatedTask() {
		return archiveObfuscatedTask;
	}
}
