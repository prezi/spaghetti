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
		List<String> sortedExpternalDependencies = sortExternalDependencies(externalDependencies);
		for (String dep : sortedExpternalDependencies) {
			if (dep.contains(".")) {
				List<String> endLines = new ArrayList<String>();
				String[] split = dep.split("\\.");
				for (int i = 0; i < split.length; i++) {
					String name = split[i];
					String path = i == 0 ? name : split[i-1] + "." + split[i];
					if (i == split.length - 1) {
						externalDependencyLines.add(path + " = " + "dependencies[" + (ix++) + "];");
					} else {
						externalDependencyLines.add("var " + name + ";");
						if (i == 0) {
							externalDependencyLines.add("(function (" + name + ", dependencies) {");
							endLines.add("})(" + name + " || (" + name + " = {}), arguments);");
						} else {
							externalDependencyLines.add("(function (" + name + ") {");
							endLines.add("})(" + name + " = " + path + " || (" + path + " = {}));");
						}
					}
				}
				Collections.reverse(endLines);
				externalDependencyLines.addAll(endLines);
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
