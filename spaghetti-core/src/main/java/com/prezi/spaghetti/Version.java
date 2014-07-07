package com.prezi.spaghetti;

import java.io.IOException;
import java.util.Properties;

public final class Version {
	private static String loadVersion() {
		Properties props = new Properties();
		try {
			props.load(Version.class.getResourceAsStream("/spaghetti.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load spaghetti.properties");
		}
		return props.getProperty("version");
	}

	public static String SPAGHETTI_VERSION = loadVersion();
}
