package com.prezi.spaghetti.gradle

import org.gradle.runtime.base.Binary

/**
 * Created by lptr on 11/02/14.
 */
interface SpaghettiCompatibleJavaScriptBinary extends Binary {
	File getJavaScriptFile()
	File getSourceMapFile()
	BundleModule getBundleTask()
	void setBundleTask(BundleModule bundleTask)
	ObfuscateModule getObfuscateTask()
	void setObfuscateTask(ObfuscateModule obfuscateModule)
	boolean isUsedForTesting()
}
