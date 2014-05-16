module Spaghetti {
    declare var __config:any;

    var moduleBaseUrl:string = __config["__baseUrl"];

    /**
   	 * Returns a URL pointing to this module's given resource.
   	 */
    export function getResourceUrl(resource:string):string {
        if (resource.substr(0, 1) == "/") {
            resource = resource.substr(1);
        }
        return moduleBaseUrl + resource;
    }
}
