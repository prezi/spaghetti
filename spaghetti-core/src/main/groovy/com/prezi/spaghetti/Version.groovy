package com.prezi.spaghetti

/**
 * Created by lptr on 28/01/14.
 */
final class Version {
	public static String SPAGHETTI_VERSION = loadVersion()

	private static String loadVersion() {
		def props = new Properties()
		props.load(Version.getResourceAsStream("/spaghetti.properties"))
		return props.getProperty("version")
	}
}
