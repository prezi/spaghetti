package com.prezi.spaghetti.gradle

import org.gradle.language.base.Binary

interface SpaghettiCompatibleJavaScriptBinary extends Binary {
	File getJavaScriptFile()
	File getSourceMapFile()
	BundleModule getBundleTask()
	void setBundleTask(BundleModule bundleTask)
	ObfuscateModule getObfuscateTask()
	void setObfuscateTask(ObfuscateModule obfuscateModule)
	boolean isUsedForTesting()
}
