package com.prezi.spaghetti;

import java.io.IOException;
import java.util.Properties;

public final class Version {
	private static Properties props = loadProperties();
	private static Properties loadProperties() {
		Properties props = new Properties();
		try {
			props.load(Version.class.getResourceAsStream("/spaghetti.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load spaghetti.properties");
		}
		return props;
	}

	public static String SPAGHETTI_VERSION = props.getProperty("version");
	public static String SPAGHETTI_BUILD = props.getProperty("build");
}
