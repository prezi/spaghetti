package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ModuleBundleLookup {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleLookup.class);

	public static ModuleBundleLookupResult lookup(Collection<File> directDependencies, Collection<File> potentialTransitiveDependencies) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up modules:");
			logger.debug("\tDirect dependencies:\n\t\t{}", Joiner.on("\n\t\t").join(directDependencies));
			logger.debug("\tPotential transitive dependencies:\n\t\t{}", Joiner.on("\n\t\t").join(potentialTransitiveDependencies));
		}

		Map<String, ModuleBundle> moduleLookup = Maps.newHashMap();
		Set<ModuleBundle> directBundles = Sets.newTreeSet();
		for (File file : directDependencies) {
			ModuleBundle bundle = tryLoadBundle(file);
			if (bundle != null) {
				moduleLookup.put(bundle.getName(), bundle);
				directBundles.add(bundle);
			}
		}

		Set<ModuleBundle> transitiveBundles = Sets.newTreeSet();
		Set<File> transitiveDependencies = Sets.newLinkedHashSet(potentialTransitiveDependencies);
		transitiveDependencies.removeAll(directDependencies);
		for (File file : transitiveDependencies) {
			ModuleBundle bundle = tryLoadBundle(file);
			if (bundle != null && !moduleLookup.containsKey(bundle.getName())) {
				transitiveBundles.add(bundle);
			}
		}

		return new ModuleBundleLookupResult(directBundles, transitiveBundles);
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
