package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundleLoader;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.file.ConfigurableFileCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

public class ModuleBundleLookup {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleLookup.class);

	public static ModuleBundleSet lookup(Project project, ConfigurableFileCollection dependencies) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up modules:");
			logger.debug("\tDependencies:\n\t\t{}", Joiner.on("\n\t\t").join(dependencies));
		}

		Set<File> directBundles = Sets.newLinkedHashSet();
		Set<File> transitiveBundles = Sets.newLinkedHashSet();

		Set<Object> dependencyObjects = dependencies.getFrom();
		for (Object from : dependencyObjects) {
			addFiles(project, from, directBundles, transitiveBundles);
		}

		return ModuleBundleLoader.loadBundles(directBundles, transitiveBundles);
	}

	private static void addFiles(Project project, Object from, Set<File> directFiles, Set<File> transitiveFiles) throws IOException {
		if (from == null) {
			return;
		}

		if (from instanceof Configuration) {
			Configuration config = (Configuration) from;
			Set<ResolvedDependency> firstLevelDependencies = config.getResolvedConfiguration().getFirstLevelModuleDependencies();
			addAllFilesFrom(firstLevelDependencies, directFiles);
			addAllFilesFromChildren(firstLevelDependencies, transitiveFiles);
		} else if (from instanceof Collection) {
			for (Object child : ((Collection<?>) from)) {
				addFiles(project, child, directFiles, transitiveFiles);
			}
		} else if (from.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(from); i++) {
				addFiles(project, Array.get(from, i), directFiles, transitiveFiles);
			}
		} else if (from instanceof Callable) {
			try {
				addFiles(project, ((Callable) from).call(), directFiles, transitiveFiles);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		} else {
			for (File file : project.files(from)) {
				directFiles.add(file);
			}
		}
	}

	private static void addAllFilesFromChildren(Set<ResolvedDependency> dependencies, Set<File> files) throws IOException {
		for (ResolvedDependency dependency : dependencies) {
			addAllFilesFrom(dependency.getChildren(), files);
			addAllFilesFromChildren(dependency.getChildren(), files);
		}
	}

	private static void addAllFilesFrom(Set<ResolvedDependency> dependencies, Set<File> files) throws IOException {
		for (ResolvedDependency dependency : dependencies) {
			for (ResolvedArtifact artifact : dependency.getModuleArtifacts()) {
				files.add(artifact.getFile());
			}
		}
	}
}
