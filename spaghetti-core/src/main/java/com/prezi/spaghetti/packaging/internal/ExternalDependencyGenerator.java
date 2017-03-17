package com.prezi.spaghetti.packaging.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class ExternalDependencyGenerator {

	public static List<String> getImportedVarNames(Collection<String> externalDependencies) {
		List<String> sortedExternalDependencies = sortExternalDependencies(externalDependencies);
		LinkedHashSet<String> uniqueExternalDependencies = new LinkedHashSet<String>();
		for (String dependency: sortedExternalDependencies) {
			if (dependency.contains(".")) {
				uniqueExternalDependencies.add(dependency.split("\\.")[0]);
			} else {
				uniqueExternalDependencies.add(dependency);
			}
		}
		return new ArrayList<String>(uniqueExternalDependencies);
	}

	static List<String> generateExternalDependencyLines(Collection<String> externalDependencies) {
		int ix = 0;
		LinkedList<String> externalDependencyLines = new LinkedList<String>();
		for (String dep : sortExternalDependencies(externalDependencies)) {
			if (dep.contains(".")) {
				String[] split = dep.split("\\.");
				String path = split[0];

				externalDependencyLines.add(String.format("var %s = (%s || {});", path, path));
				for (int i = 1; i < split.length - 1; i++) {
					path += "." + split[i];
					externalDependencyLines.add(String.format("%s = (%s || {});", path, path));
				}
				externalDependencyLines.add(String.format("%s = arguments[%d];", dep, ix++));
			} else {
				externalDependencyLines.add(String.format("var %s=arguments[%d];", dep, ix++));
			}
		}
		return externalDependencyLines;
	}

	private static List<String> sortExternalDependencies(Collection<String> externalDependencies) {
		List<String> sortedExternalDependencies = new ArrayList<String>(externalDependencies);
		Collections.sort(sortedExternalDependencies);
		return sortedExternalDependencies;
	}

}
