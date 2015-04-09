package com.prezi.spaghetti.bundle.internal;

import java.io.File;
import java.util.Set;

public class DependentFiles {

	private Set<File> directFiles;
	private Set<File> transitiveFiles;

	public DependentFiles(Set<File> directFiles, Set<File> transitiveFiles) {
		this.directFiles = directFiles;
		this.transitiveFiles = transitiveFiles;
	}

	public Set<File> getDirectFiles() {
		return directFiles;
	}

	public Set<File> getTransitiveFiles() {
		return transitiveFiles;
	}
}
