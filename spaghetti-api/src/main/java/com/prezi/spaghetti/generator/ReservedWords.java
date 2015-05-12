package com.prezi.spaghetti.generator;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Reserved words used by Spaghetti.
 */
public class ReservedWords {
	public static final String MODULE_WRAPPER_FUNCTION = "module";

	public static final String SPAGHETTI_CLASS = "Spaghetti";

	@ProtectedWord
	public static final String GET_MODULE_VERSION = "getModuleVersion";

	@ProtectedWord
	public static final String GET_SPAGHETTI_VERSION = "getSpaghettiVersion";

	@ProtectedWord
	public static final String GET_RESOURCE_URL = "getResourceUrl";

	@ProtectedWord
	public static final String GET_MODULE_NAME = "getModuleName";

	public static final String MODULE = "module";

	public static final String DEPENDENCIES = "dependencies";

	public static final SortedSet<String> PROTECTED_WORDS = gatherProtectedWords();

	private static SortedSet<String> gatherProtectedWords() {
		SortedSet<String> result = new TreeSet<String>();
		for (Field field : ReservedWords.class.getDeclaredFields()) {
			if (field.getType().equals(String.class) && field.getAnnotation(ProtectedWord.class) != null) {
				try {
					result.add((String) field.get(ReservedWords.class));
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Could not access " + ReservedWords.class.getName() + "." + field.getName());
				}
			}
		}
		return Collections.unmodifiableSortedSet(result);
	}
}
