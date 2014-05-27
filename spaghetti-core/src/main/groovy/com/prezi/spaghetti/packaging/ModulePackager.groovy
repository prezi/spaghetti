package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle

/**
 * Created by lptr on 27/05/14.
 */
interface ModulePackager {
	void packageModuleDirectory(File outputDirectory, ModulePackageParameters params)
	void packageModuleZip(File outputFile, ModulePackageParameters params)
	String getModuleName(ModuleBundle bundle)
}
