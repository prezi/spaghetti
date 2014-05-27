package com.prezi.spaghetti

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by lptr on 16/05/14.
 */
class ReservedWords {
	@Protected
	public static final String INSTANCE = "__instance"

	@Protected
	public static final String STATIC = "__static"

	@Protected
	public static final String SPAGHETTI_WRAPPER_FUNCTION = "__spaghetti"

	public static final String CONFIG = "SpaghettiConfiguration"

	@Protected
	public static final String GET_RESOURCE_URL_FUNCTION = "getResourceUrl"

	@Protected
	public static final String GET_NAME_FUNCTION = "getName"

	public static final String MODULES = "__modules"

	public static final String BASE_URL = "__baseUrl"

	public static final String SPAGHETTI_MODULE_CONFIGURATION = "SpaghettiConfiguration"

	// Words to be protected against obfuscation
	public static final SortedSet<String> PROTECTED_WORDS = new TreeSet<>(ReservedWords.declaredFields.findAll {
		return it.type == String && it.getAnnotation(Protected)
	}.collect { (String) it.get(ReservedWords) }).asImmutable()
}

/**
 * Protect word against obfuscation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@interface Protected {}
