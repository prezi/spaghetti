class Spaghetti {
	static var moduleBaseUrl:String = createModuleBaseUrl();

	static function createModuleBaseUrl() {
		var moduleUrl = untyped __modules["require"].toUrl("${moduleName}.js");
		var lastIndex = moduleUrl.lastIndexOf("/");
		return moduleUrl.substr(0, lastIndex + 1);
	}

	/**
	 * Returns a URL pointing to this module's given resource.
	 */
	public static function getResourceUrl(resource:String):String {
		if (resource.substr(0, 1) == "/") {
			resource = resource.substr(1);
		}
		return moduleBaseUrl + resource;
	}
}
