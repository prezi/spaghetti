package com.prezi.spaghetti.bundle;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.internal.DefaultModuleBundleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ModuleBundleLoader {
	private  static final Logger logger = LoggerFactory.getLogger(ModuleBundleLoader.class);

	public static ModuleBundleSet loadBundles(Collection<File> directBundleFiles, Collection<File> transitiveBundleFiles) throws IOException {
		Set<ModuleBundle> directBundles = loadBundles(directBundleFiles);
		Set<ModuleBundle> transitiveBundles = loadBundles(transitiveBundleFiles);
		transitiveBundles = filterTransitiveDependencies(directBundles, transitiveBundles);

		return new DefaultModuleBundleSet(directBundles, transitiveBundles);
	}

	public static<T> Set<T> filterTransitiveDependencies(Set<T> directDependencies, Set<T> transitiveDependencies) {
		Sets.SetView<T> sharedModules = Sets.intersection(directDependencies, transitiveDependencies);
		Set<T> filtered = new HashSet<T>(transitiveDependencies);
		if (!sharedModules.isEmpty()) {
			logger.info("Some modules appear both as direct and as transitive dependencies, treating them as direct dependencies: {}", sharedModules);
			filtered.removeAll(directDependencies);
		}
		return filtered;
	}

	private static Set<ModuleBundle> loadBundles(Collection<File> bundleFiles) throws IOException {
		Set<ModuleBundle> bundles = Sets.newLinkedHashSet();
		for (File bundleFile : bundleFiles) {
			logger.debug("Trying to load module bundle from {}", bundleFile);
			try {
				ModuleBundle bundle = ModuleBundleFactory.load(bundleFile);
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
