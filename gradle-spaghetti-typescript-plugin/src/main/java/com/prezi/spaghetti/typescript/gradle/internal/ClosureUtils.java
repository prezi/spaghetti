package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

class ClosureUtils {
	public static void writeMainEntryPoint(File file, Collection<File> entryPoints, String namespace) throws IOException {
		String data;
		if (entryPoints.size() == 1) {
			File entryPoint = Iterables.getOnlyElement(entryPoints);
			data = String.format(
				"%s=require('%s');",
				namespace,
				getRelativeImport(file, entryPoint));
		} else {
			Collection<String> requireCalls = Lists.newArrayList();
			for (File entryPoint: entryPoints) {
				requireCalls.add(String.format("require('%s')", getRelativeImport(file, entryPoint)));
			}
			data = String.format(
				"%s=[%s];",
				namespace,
				Joiner.on(",").join(requireCalls));
		}
		FileUtils.write(file, data);
	}

	private static String getRelativeImport(File base, File imported) {
		Path basePath = Paths.get(base.getParentFile().toURI());
		Path importedPath = Paths.get(imported.toURI());
		Path relativized = basePath.relativize(importedPath);

		List<String> parts = Lists.newArrayList();
		for (Path part: relativized) {
			parts.add(part.toFile().getName());
		}

		String rel = Joiner.on("/").join(parts);
		if (!rel.startsWith("../")) {
			rel = "./" + rel;
		}
		return FilenameUtils.removeExtension(rel);
	}
}
