interface Spaghetti {
	[s: string]: any;

	/**
	 * Returns the module version.
	 */
	getModuleVersion():string;

	/**
	 * Returns the Spaghetti version used to build the module.
	 */
	getSpaghettiVersion():string;

	/**
	 * Returns the name of the module.
	 */
	getModuleName():string;

	/**
	 * Returns a URL pointing to this module's given resource.
	 */
	getResourceUrl(resource:string):string;
}

declare var Spaghetti: Spaghetti;
