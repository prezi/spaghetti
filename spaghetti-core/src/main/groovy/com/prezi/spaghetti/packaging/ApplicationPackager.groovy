package com.prezi.spaghetti.packaging
/**
 * Created by lptr on 16/05/14.
 */
interface ApplicationPackager {
	void packageApplicationDirectory(File outputDirectory, ApplicationPackageParameters params)
	void packageApplicationZip(File outputFile, ApplicationPackageParameters params)
}
