package com.prezi.spaghetti.gradle

import org.gradle.api.Nullable
import org.gradle.language.base.internal.BinaryNamingScheme

/**
 * Created by lptr on 29/04/14.
 */
class SpaghettiCompatibleBinaryNamingScheme implements BinaryNamingScheme {
	private final String parentName;
	private final String collapsedName;

	public SpaghettiCompatibleBinaryNamingScheme(String parentName) {
		this.parentName = parentName;
		this.collapsedName = collapseMain(this.parentName);
	}

	private static String collapseMain(String name) {
		return name.equals("main") ? "" : name;
	}

	@Override
	public String getDescription() {
		return String.format("%s binary", parentName);
	}

	@Override
	public String getLifecycleTaskName() {
		return makeName(parentName, "module");
	}

	@Override
	public String getTaskName(@Nullable String verb) {
		return getTaskName(verb, null);
	}

	@Override
	public String getTaskName(@Nullable String verb, @Nullable String target) {
		return makeName(verb, collapsedName, "module", target);
	}

	private static String makeName(String... words) {
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			if (word == null || word.length() == 0) {
				continue;
			}
			if (builder.length() == 0) {
				appendUncapitalized(builder, word);
			} else {
				appendCapitalized(builder, word);
			}
		}
		return builder.toString();
	}

	private static void appendCapitalized(StringBuilder builder, String word) {
		builder.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1));
	}

	private static void appendUncapitalized(StringBuilder builder, String word) {
		builder.append(Character.toLowerCase(word.charAt(0))).append(word.substring(1));
	}

	@Override
	public String getOutputDirectoryBase() {
		return parentName
	}

	// Omitting @Override to stay compatible with Gradle 1.11
	@SuppressWarnings("GrMethodMayBeStatic")
	List<String> getVariantDimensions() {
		return []
	}
}
