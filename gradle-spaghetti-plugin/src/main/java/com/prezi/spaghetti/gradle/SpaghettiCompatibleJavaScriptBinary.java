package com.prezi.spaghetti.gradle;

import org.gradle.language.base.Binary;

import java.io.File;

public interface SpaghettiCompatibleJavaScriptBinary extends Binary {
	File getJavaScriptFile();
	File getSourceMapFile();
	BundleModule getBundleTask();
	void setBundleTask(BundleModule bundleTask);
	@SuppressWarnings("UnusedDeclaration")
	ObfuscateModule getObfuscateTask();
	void setObfuscateTask(ObfuscateModule obfuscateModule);
	boolean isUsedForTesting();
}
