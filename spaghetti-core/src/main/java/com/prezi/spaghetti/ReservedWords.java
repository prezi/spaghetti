package com.prezi.spaghetti;

import com.google.common.collect.ImmutableSortedSet;

import java.lang.reflect.Field;
import java.util.SortedSet;

public class ReservedWords {

	@Protected
	public static final String INSTANCE = "__instance";

	@Protected
	public static final String STATIC = "__static";

	@Protected
	public static final String SPAGHETTI_WRAPPER_FUNCTION = "__spaghetti";
	public static final String CONFIG = "SpaghettiConfiguration";

	@Protected
	public static final String GET_RESOURCE_URL_FUNCTION = "getResourceUrl";

	@Protected
	public static final String GET_NAME_FUNCTION = "getName";
	public static final String MODULES = "__modules";
	public static final String BASE_URL = "__baseUrl";
	public static final String SPAGHETTI_MODULE_CONFIGURATION = "SpaghettiConfiguration";
	public static final SortedSet<String> PROTECTED_WORDS = gatherProtectedWords();

	private static SortedSet<String> gatherProtectedWords() {
		ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.naturalOrder();
		for (Field field : ReservedWords.class.getDeclaredFields()) {
			if (field.getType().equals(String.class) && field.getAnnotation(Protected.class) != null) {
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
