package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import groovy.transform.TupleConstructor
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.artifacts.ResolvedModuleVersion
import org.slf4j.LoggerFactory

/**
 * Created by lptr on 17/11/13.
 */
class ModuleBundleLookup {
	private static final logger = LoggerFactory.getLogger(ModuleBundleLookup)

	public static ModuleBundleLookupResult lookupFromConfiguration(Configuration configuration) {
		logger.debug "Scanning direct dependencies"
		def resolvedConfig = configuration.resolvedConfiguration
		Set<ModuleVersionIdentifier> directDependencyVersions = []
		Set<ModuleBundle> directModules = new TreeSet<>()
		resolvedConfig.firstLevelModuleDependencies.each { ResolvedDependency dep ->
			directDependencyVersions.add dep.module.id
			dep.moduleArtifacts.each { artifact ->
				tryLoadBundle(artifact.file, directModules)
			}
		}
		logger.debug "Scanning transitive dependencies"
		Set<ModuleBundle> transitiveModules = new TreeSet<>()
		resolvedConfig.resolvedArtifacts.each { ResolvedArtifact artifact ->
			if (!directDependencyVersions.contains(artifact.moduleVersion.id)) {
				tryLoadBundle(artifact.file, transitiveModules)
			}
			return []
		}
		return new ModuleBundleLookupResult(
				directBundles: directModules,
				transitiveBundles: transitiveModules
		)
	}

	public static Set<ModuleBundle> lookup(Iterable<File> files) {
		Set<ModuleBundle> bundles = new TreeSet<>()
		files.each { tryLoadBundle(it, bundles) }
		return bundles
	}

	private static void tryLoadBundle(File file, Collection<ModuleBundle> bundles) {
		logger.debug("Trying to load module bundle from ${file}")
		try {
			def bundle = ModuleBundle.load(file)
			logger.info "Found module bundle {}", bundle.name
			bundles.add bundle
		} catch (ex) {
			logger.debug "Not a module bundle: {}: {}", file, ex
			logger.trace "Exception", ex
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
