package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.internal.AbstractBundleModuleTask;

import java.io.File;
import java.util.concurrent.Callable;

public class BundleModule extends AbstractBundleModuleTask {
	public BundleModule() {
		this.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/bundle");
			}

		});
	}
}
