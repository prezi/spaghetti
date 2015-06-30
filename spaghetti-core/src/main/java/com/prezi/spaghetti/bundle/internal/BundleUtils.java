package com.prezi.spaghetti.bundle.internal;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BundleUtils {

	private static final Pattern EXTERNAL_LIB = Pattern.compile("([^:]+)(?::(.+))?");

	public static Map<String, String> parseExternalDependencies(String dependencyList) {
		return Strings.isNullOrEmpty(dependencyList) ?
				Maps.<String, String>newLinkedHashMap() :
				parseExternalDependencies(Splitter.on(',').split(dependencyList));
	}

	public static Map<String, String> parseExternalDependencies(Iterable<String> dependencyList) {
		Map<String, String> externalDependencies = Maps.newLinkedHashMap();
		for (String extDep : dependencyList) {
			Map.Entry<String, String> parsedDeclaration = parseExternalDependencyPair(extDep);
			externalDependencies.put(parsedDeclaration.getKey(), parsedDeclaration.getValue());
		}
		return externalDependencies;
	}

	public static Map.Entry<String, String> parseExternalDependencyPair(String colonPair) {
		Matcher matcher = EXTERNAL_LIB.matcher(colonPair);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Incorrect format for external dependency " + colonPair + ", use 'varname:dependency'");
		}
		String name = matcher.group(1);
		String path = matcher.group(2);
		if (path == null) path = name;
		return new AbstractMap.SimpleEntry<String, String>(name, path);
	}
}
