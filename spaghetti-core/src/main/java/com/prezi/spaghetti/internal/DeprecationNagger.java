package com.prezi.spaghetti.internal;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DeprecationNagger {
	private static final Logger logger = LoggerFactory.getLogger(DeprecationNagger.class);
	private static final Set<String> alreadyNagged = Sets.newConcurrentHashSet();

	private static String getDeprecationMessage() {
		return "has been deprecated";
	}

	public static void nagUserOfReplacedMethod(String methodName, String replacement) {
		nagUserWith(String.format(
				"The %s method %s. Please use the %s method instead.",
				methodName, getDeprecationMessage(), replacement));
	}

	public static void nagUserOfReplacedProperty(String propertyName, String replacement) {
		nagUserWith(String.format(
				"The %s property %s. Please use the %s property instead.",
				propertyName, getDeprecationMessage(), replacement));
	}

	public static void nagUserOfDiscontinuedMethod(String methodName) {
		nagUserWith(String.format("The %s method %s.",
				methodName, getDeprecationMessage()));
	}

	public static void nagUserOfDiscontinuedProperty(String propertyName, String advice) {
		nagUserWith(String.format("The %s property %s. %s",
				propertyName, getDeprecationMessage(), advice));
	}

	public static void nagUserOfReplacedNamedParameter(String parameterName, String replacement) {
		nagUserWith(String.format(
				"The %s named parameter %s. Please use the %s named parameter instead.",
				parameterName, getDeprecationMessage(), replacement));
	}

	/**
	 * Try to avoid using this nagging method. The other methods use a consistent wording for when things will be removed.
	 */
	public static void nagUserWith(String message) {
		if (alreadyNagged.add(message)) {
			logger.warn(message);
		}
	}

	/**
	 * Avoid using this method, use the variant with an explanation instead.
	 */
	public static void nagUserOfDeprecated(String thing) {
		nagUserWith(String.format("%s %s", thing, getDeprecationMessage()));
	}

	public static void nagUserOfDeprecated(String thing, String explanation) {
		nagUserWith(String.format("%s %s. %s.", thing, getDeprecationMessage(), explanation));
	}
}
