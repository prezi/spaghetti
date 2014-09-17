package com.prezi.spaghetti.generator;

import com.google.common.collect.ImmutableSortedSet;

import java.lang.reflect.Field;
import java.util.SortedSet;

/**
 * Reserved words used by Spaghetti.
 */
public class ReservedWords {
	public static final String MODULE_WRAPPER_FUNCTION = "module";

	public static final String SPAGHETTI_CLASS = "Spaghetti";
	public static final String SPAGHETTI_PARAMETERS_CLASS = "SpaghettiParameters";

	@ProtectedWord
	public static final String GET_MODULE_VERSION = "getModuleVersion";

	@ProtectedWord
	public static final String GET_SPAGHETTI_VERSION = "getSpaghettiVersion";

	@ProtectedWord
	public static final String GET_RESOURCE_URL = "getResourceUrl";

	@ProtectedWord
	public static final String GET_MODULE_NAME = "getModuleName";

	@ProtectedWord
	public static final String GET_PARAMETER = "getParameter";

	public static final String MODULE = "module";

	public static final String DEPENDENCIES = "dependencies";

	public static final SortedSet<String> PROTECTED_WORDS = gatherProtectedWords();

	private static SortedSet<String> gatherProtectedWords() {
		ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.naturalOrder();
		for (Field field : ReservedWords.class.getDeclaredFields()) {
			if (field.getType().equals(String.class) && field.getAnnotation(ProtectedWord.class) != null) {
				try {
					builder.add((String) field.get(ReservedWords.class));
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Could not access " + ReservedWords.class.getName() + "." + field.getName());
				}
			}
		}
		return builder.build();
	}
}
