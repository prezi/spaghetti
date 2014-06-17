package com.prezi.spaghetti.packaging
interface ApplicationPackager {
	void packageApplicationDirectory(File outputDirectory, ApplicationPackageParameters params)
	void packageApplicationZip(File outputFile, ApplicationPackageParameters params)
}
