package com.prezi.spaghetti.haxe.gradle;


import java.io.File;

public class SpaghettiHaxeExtension {

	private File munitNodeModuleInstallDir;

	public File getMunitNodeModuleInstallDir() {
		return munitNodeModuleInstallDir;
	}

	public void setMunitNodeModuleInstallDir(File munitNodeModuleInstallDir) {
		this.munitNodeModuleInstallDir = munitNodeModuleInstallDir;
	}
}
