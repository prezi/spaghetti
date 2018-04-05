package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.bundle.internal.ModuleBundleLoader;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class ModuleBundleLookup {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleLookup.class);

	public static ModuleBundleSet lookup(Project project, Object dependencies) throws IOException {
		Set<File> directFiles = Sets.newLinkedHashSet();
		Set<File> transitiveFiles = Sets.newLinkedHashSet();
		Map<ResolvedDependency, List<File>> moduleFileCache = Maps.newHashMap();

		addFiles(project, dependencies, moduleFileCache, directFiles, transitiveFiles);

		if (logger.isDebugEnabled()) {
			logger.debug("Loading modules from:");
			logger.debug("\tDirect dependencies:\n\t\t{}", Joiner.on("\n\t\t").join(directFiles));
			logger.debug("\tTransitive dependencies:\n\t\t{}", Joiner.on("\n\t\t").join(transitiveFiles));
		}

		return ModuleBundleLoader.loadBundles(directFiles, transitiveFiles);
	}

	private static void addFiles(Project project, Object from, Map<ResolvedDependency, List<File>> moduleFileCache, Set<File> directFiles, Set<File> transitiveFiles) throws IOException {
		if (from == null) {
			return;
		}

		if (from instanceof Configuration) {
			Configuration config = (Configuration) from;
			Set<ResolvedDependency> firstLevelDependencies = config.getResolvedConfiguration().getFirstLevelModuleDependencies();
			addAllFilesFrom(firstLevelDependencies, moduleFileCache, directFiles);
			addAllFilesFromChildren(firstLevelDependencies, moduleFileCache, transitiveFiles);
		} else if (from instanceof ConfigurableFileCollection) {
			for (Object child : ((ConfigurableFileCollection) from).getFrom()) {
				addFiles(project, child, moduleFileCache, directFiles, transitiveFiles);
			}
		} else if (from instanceof FileCollection) {
			directFiles.addAll(((FileCollection) from).getFiles());
		} else if (from instanceof Collection) {
			for (Object child : ((Collection<?>) from)) {
				addFiles(project, child, moduleFileCache, directFiles, transitiveFiles);
			}
		} else if (from.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(from); i++) {
				addFiles(project, Array.get(from, i), moduleFileCache, directFiles, transitiveFiles);
			}
		} else if (from instanceof Callable) {
			try {
				addFiles(project, ((Callable) from).call(), moduleFileCache, directFiles, transitiveFiles);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		} else if (from instanceof File) {
			directFiles.add((File) from);
		} else {
			for (File file : project.files(from)) {
				directFiles.add(file);
			}
		}
	}

	private static void addAllFilesFromChildren(Set<ResolvedDependency> dependencies, Map<ResolvedDependency, List<File>> moduleFileCache, Set<File> files) throws IOException {
		for (ResolvedDependency dependency : dependencies) {
			addAllFilesFrom(dependency.getChildren(), moduleFileCache, files);
			addAllFilesFromChildren(dependency.getChildren(), moduleFileCache, files);
		}
	}

	private static void addAllFilesFrom(Set<ResolvedDependency> dependencies, Map<ResolvedDependency, List<File>> moduleFileCache, Set<File> files) throws IOException {
		for (ResolvedDependency dependency : dependencies) {
			if (!moduleFileCache.containsKey(dependency)) {
				List<File> values = Lists.newArrayList();
				moduleFileCache.put(dependency, values);
				for (ResolvedArtifact artifact : dependency.getModuleArtifacts()) {
					values.add(artifact.getFile());
				}
			}
			files.addAll(moduleFileCache.get(dependency));
		}
	}
}
