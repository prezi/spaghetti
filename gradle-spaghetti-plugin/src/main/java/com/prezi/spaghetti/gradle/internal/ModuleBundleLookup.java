package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ModuleBundleLookup {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleLookup.class);

	public static Set<ModuleBundle> lookup(Iterable<File> dependencies) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up modules:");
			logger.debug("\tDependencies:\n\t\t{}", Joiner.on("\n\t\t").join(dependencies));
		}

		Set<ModuleBundle> bundles = Sets.newTreeSet();
		for (File file : dependencies) {
			ModuleBundle bundle = tryLoadBundle(file);
			if (bundle != null) {
				bundles.add(bundle);
			}
		}

		return bundles;
	}

	private static ModuleBundle tryLoadBundle(File file) throws IOException {
		logger.debug("Trying to load module bundle from {}", file);
		try {
			ModuleBundle bundle = ModuleBundleFactory.load(file);
			logger.info("Found module bundle {}", bundle.getName());
			return bundle;
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.debug("Not a module bundle: {}: {}", file, ex);
			logger.trace("Exception", ex);
			return null;
		}
	}
}
