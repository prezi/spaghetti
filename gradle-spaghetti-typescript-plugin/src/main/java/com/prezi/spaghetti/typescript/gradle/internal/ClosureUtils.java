package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.io.FileUtils;

class ClosureUtils {
	public static void writeMainEntryPoint(File file, Collection<File> entryPoints, String namespace) throws IOException {
		String data;
		if (entryPoints.size() == 1) {
			File entryPoint = Iterables.getOnlyElement(entryPoints);
			data = String.format(
				"%s=require('%s');",
				namespace,
				entryPoint.getAbsolutePath());
		} else {
			Collection<String> requireCalls = Lists.newArrayList();
			for (File entryPoint: entryPoints) {
				requireCalls.add(String.format("require('%s')", entryPoint.getAbsolutePath()));
			}
			data = String.format(
				"%s=[%s];",
				namespace,
				Joiner.on(",").join(requireCalls));
		}
		FileUtils.write(file, data);
	}
}