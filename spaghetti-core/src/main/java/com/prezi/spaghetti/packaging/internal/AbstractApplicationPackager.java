package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.packaging.ApplicationPackageParameters;
import com.prezi.spaghetti.packaging.ApplicationPackager;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryWriter;
import com.prezi.spaghetti.structure.internal.StructuredWriter;
import com.prezi.spaghetti.structure.internal.StructuredZipWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractApplicationPackager implements ApplicationPackager {
	private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationPackager.class);

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

		Map<String, String> unmetExternals = new HashMap<String, String>();
		for (ModuleBundle bundle : params.bundles) {
			for (String external : bundle.getExternalDependencies()) {
				if (!params.externals.containsKey(external)) unmetExternals.put(bundle.getName(), external);
			}
		}
		if (!unmetExternals.isEmpty()) {
			logger.warn("Some modules have external dependencies without a specified path:");
			for (Map.Entry<String, String> unmetExternal : unmetExternals.entrySet()) {
				logger.warn("\t{} depends on {}", unmetExternal.getKey(), unmetExternal.getValue());
			}
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
