interface SpaghettiModuleConfiguration {
    /**
   	 * Returns the name of the module.
   	 */
    getName():string;

    /**
   	 * Returns a URL pointing to this module's given resource.
   	 */
    getResourceUrl(resource:string):string;
}
