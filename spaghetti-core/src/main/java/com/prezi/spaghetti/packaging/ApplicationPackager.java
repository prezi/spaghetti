package com.prezi.spaghetti.packaging;

import java.io.File;
import java.io.IOException;

public interface ApplicationPackager {
	void packageApplicationDirectory(File outputDirectory, ApplicationPackageParameters params) throws IOException;

	void packageApplicationZip(File outputFile, ApplicationPackageParameters params) throws IOException;
}
