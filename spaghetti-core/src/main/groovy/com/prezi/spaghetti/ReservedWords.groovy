package com.prezi.spaghetti

/**
 * Created by lptr on 16/05/14.
 */
class ReservedWords {
	public static final String MODULE = "__module"
	public static final String CONSTANTS = "__consts"

	// Words to be protected against obfuscation
	public static final List<String> PROTECTED_WORDS = [
	        MODULE, CONSTANTS
	].asImmutable()
}
