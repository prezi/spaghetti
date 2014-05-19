package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import groovy.transform.TupleConstructor
import org.slf4j.LoggerFactory

/**
 * Created by lptr on 17/11/13.
 */
class ModuleBundleLookup {
	private static final logger = LoggerFactory.getLogger(ModuleBundleLookup)

	public static ModuleBundleLookupResult lookup(Collection<File> directDependencies, Collection<File> potentialTransitiveDependencies) {
		if (logger.isDebugEnabled()) {
			logger.debug "Looking up modules:"
			logger.debug "\tDirect dependencies:\n\t\t${directDependencies.join("\n\t\t")}"
			logger.debug "\tPotential transitive dependencies:\n\t\t${potentialTransitiveDependencies.join("\n\t\t")}"
		}
		Map<String, ModuleBundle> moduleLookup = [:]
		Set<ModuleBundle> directBundles = new TreeSet<>()
		directDependencies.each { file ->
			def bundle = tryLoadBundle(file)
			if (bundle) {
				moduleLookup.put bundle.name, bundle
				directBundles.add bundle
			}
		}
		Set<ModuleBundle> transitiveBundles = new TreeSet<>()
		(potentialTransitiveDependencies - directDependencies).each { file ->
			def bundle = tryLoadBundle(file)
			if (bundle && !moduleLookup.containsKey(bundle.name)) {
				transitiveBundles.add bundle
			}
		}
		return new ModuleBundleLookupResult(
				directBundles: directBundles,
				transitiveBundles: transitiveBundles
		)
	}

	private static ModuleBundle tryLoadBundle(File file) {
		logger.debug("Trying to load module bundle from ${file}")
		try {
			def bundle = ModuleBundleFactory.load(file)
			logger.info "Found module bundle {}", bundle.name
			return bundle
		} catch (ex) {
			// TODO Re-throw FileNotFoundExcepiton (or even IOException)
			logger.debug "Not a module bundle: {}: {}", file, ex
			logger.trace "Exception", ex
			return null
		}
	}
}

@TupleConstructor
class ModuleBundleLookupResult {
	Set<ModuleBundle> directBundles
	Set<ModuleBundle> transitiveBundles

	Set<ModuleBundle> getAllBundles() {
		return new TreeSet<>(directBundles + transitiveBundles)
	}
}
