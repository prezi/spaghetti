package com.prezi.spaghetti.bundle.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.bundle.ModuleBundleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ModuleBundleLoader {
	private  static final Logger logger = LoggerFactory.getLogger(ModuleBundleLoader.class);

	public static ModuleBundleSet loadBundles(Collection<File> directBundleFiles, Collection<File> lazyBundleFiles, Collection<File> transitiveBundleFiles, ModuleBundleType moduleBundleType) throws IOException {
		Set<ModuleBundle> directBundles = loadBundles(directBundleFiles, moduleBundleType);
		Set<ModuleBundle> lazyBundles = loadBundles(lazyBundleFiles, moduleBundleType);
		Set<ModuleBundle> transitiveBundles = loadBundles(transitiveBundleFiles, moduleBundleType);

		lazyBundles.forEach(lazyBundle -> {
			Preconditions.checkArgument(lazyBundle.isLazyLoadable(), "Lazy bundle is not marked as lazy loadable: " + lazyBundle.getName());
		});

		Sets.SetView<ModuleBundle> sharedModules = Sets.intersection(directBundles, transitiveBundles);
		if (!sharedModules.isEmpty()) {
			logger.info("Some module bundles appear both as direct and as transitive dependencies, treating them as direct dependencies: {}", sharedModules);
			transitiveBundles.removeAll(directBundles);
		}

		return new DefaultModuleBundleSet(directBundles, lazyBundles, transitiveBundles);
	}

	private static Set<ModuleBundle> loadBundles(Collection<File> bundleFiles, ModuleBundleType moduleBundleType) throws IOException {
		Set<ModuleBundle> bundles = Sets.newLinkedHashSet();
		for (File bundleFile : bundleFiles) {
			logger.debug("Trying to load module bundle from {}", bundleFile);
			try {
				ModuleBundle bundle = ModuleBundleFactory.load(bundleFile, moduleBundleType);
				logger.info("Found module bundle {}", bundle.getName());
				if (!bundles.add(bundle)) {
					logger.warn("Bundle {} loaded twice", bundle.getName());
				}
			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				logger.debug("Not a module bundle: {}: {}", bundleFile, ex);
				logger.trace("Exception", ex);
			}
		}
		return bundles;
	}
}
