package com.prezi.spaghetti.gradle;

import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.runtime.base.Binary;

import java.io.File;

public interface SpaghettiCompatibleJavaScriptBinary extends Binary {
	File getJavaScriptFile();
	File getSourceMapFile();
	BundleModule getBundleTask();
	void setBundleTask(BundleModule bundleTask);
	@SuppressWarnings("UnusedDeclaration")
	ObfuscateModule getObfuscateTask();
	void setObfuscateTask(ObfuscateModule obfuscateModule);
	AbstractArchiveTask getArchiveTask();
	void setArchiveTask(AbstractArchiveTask task);
	AbstractArchiveTask getArchiveObfuscatedTask();
	void setArchiveObfuscatedTask(AbstractArchiveTask task);
	@SuppressWarnings("UnusedDeclaration")
	boolean isUsedForTesting();
}
