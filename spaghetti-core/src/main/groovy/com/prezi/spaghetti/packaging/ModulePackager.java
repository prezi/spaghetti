package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

import java.io.File;
import java.io.IOException;

public interface ModulePackager {
	void packageModuleDirectory(File outputDirectory, ModulePackageParameters params) throws IOException;

	void packageModuleZip(File outputFile, ModulePackageParameters params) throws IOException;

	String getModuleName(ModuleBundle bundle);
}
