package com.prezi.spaghetti

final class Version {
	public static String SPAGHETTI_VERSION = loadVersion()

	private static String loadVersion() {
		def props = new Properties()
		props.load(Version.getResourceAsStream("/spaghetti.properties"))
		return props.getProperty("version")
	}
}
