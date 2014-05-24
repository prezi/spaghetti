declare class SpaghettiConfiguration {
    /**
     * Get the configuration instance.
     */
    static getInstance():SpaghettiConfiguration;

    /**
   	 * Returns the name of the module.
   	 */
    static getName():string;

    /**
   	 * Returns a URL pointing to this module's given resource.
   	 */
    static getResourceUrl(resource:string):string;
}
