package com.prezi.spaghetti.gradle;

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
