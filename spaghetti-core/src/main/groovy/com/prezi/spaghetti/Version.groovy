package com.prezi.spaghetti

/**
 * Created by lptr on 28/01/14.
 */
final class Version {
	public static String SPAGHETTI_VERSION = new ConfigSlurper().parse(Version.getResource("/spaghetti.properties")).version
}
