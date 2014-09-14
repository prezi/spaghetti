package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.packaging.ApplicationPackageParameters;
import com.prezi.spaghetti.packaging.ApplicationPackager;
import com.prezi.spaghetti.structure.StructuredWriter;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryWriter;
import com.prezi.spaghetti.structure.internal.StructuredZipWriter;

import java.io.File;
import java.io.IOException;

public abstract class AbstractApplicationPackager implements ApplicationPackager {
	@Override
	public void packageApplicationDirectory(File outputDirectory, ApplicationPackageParameters params) throws IOException {
		packageApplication(new StructuredDirectoryWriter(outputDirectory), params);
	}

	@Override
	public void packageApplicationZip(File outputFile, ApplicationPackageParameters params) throws IOException {
		packageApplication(new StructuredZipWriter(outputFile), params);
	}

	protected void packageApplication(StructuredWriter writer, final ApplicationPackageParameters params) throws IOException {
		if (params.execute && params.mainModule == null) {
			throw new IllegalArgumentException("Main bundle not set, but execute is");
		}

		if (params.mainModule != null && Iterables.find(params.bundles, new Predicate<ModuleBundle>() {
			@Override
			public boolean apply(ModuleBundle input) {
				return input.getName().equals(params.mainModule);
			}
		}, null) == null) {
			throw new IllegalArgumentException("Main bundle \"" + params.mainModule + "\" not found among bundles: " + Joiner.on(", ").join(params.bundles));
		}

		writer.init();
		try {
			packageApplicationInternal(writer, params);
		} finally {
			writer.close();
		}
	}

	public abstract void packageApplicationInternal(StructuredWriter writer, ApplicationPackageParameters params) throws IOException;
}
