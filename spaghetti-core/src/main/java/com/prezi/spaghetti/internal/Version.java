package com.prezi.spaghetti.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Version {
	private static Properties props = loadProperties();
	private static Properties loadProperties() {
		Properties props = new Properties();
		try {
			InputStream input = Version.class.getResourceAsStream("/spaghetti.properties");
			if (input != null) {
				props.load(input);
			}
		} catch (IOException ignored) {
			// Silently swallow error
		}
		return props;
	}

	public static String SPAGHETTI_VERSION = props.getProperty("version");
	public static String SPAGHETTI_BUILD = props.getProperty("build");
}
