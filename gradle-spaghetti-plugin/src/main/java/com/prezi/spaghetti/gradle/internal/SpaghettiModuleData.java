package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.BundleModule;
import com.prezi.spaghetti.gradle.ObfuscateModule;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;
import java.util.concurrent.Callable;

public interface SpaghettiModuleData {
	Callable<File> getJavaScriptFile();
	Callable<File> getSourceMapFile();

	BundleModule getBundleTask();
	ObfuscateModule getObfuscateTask();
	AbstractArchiveTask getArchiveTask();
	AbstractArchiveTask getArchiveObfuscatedTask();
}
