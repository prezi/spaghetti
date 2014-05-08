module Spaghetti {
    declare var __modules:Array<any>;

    var moduleBaseUrl:string = function() {
        var moduleUrl = __modules["require"].toUrl("${moduleName}.js");
        var lastIndex = moduleUrl.lastIndexOf("/");
        return moduleUrl.substr(0, lastIndex + 1);
    }();

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
