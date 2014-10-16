public native object Spaghetti {
	/**
	 * Returns the module version.
	 */
	fun getModuleVersion(): String = noImpl

	/**
	 * Returns the Spaghetti version used to build the module.
	 */
	fun getSpaghettiVersion(): String = noImpl

	/**
	 * Returns the name of the module.
	 */
	fun getModuleName(): String = noImpl

	/**
	 * Returns a URL pointing to this module's given resource.
	 */
	fun getResourceUrl(resource:String): String = noImpl
}
