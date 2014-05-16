class Spaghetti {
	static var moduleBaseUrl:String = untyped __js__('__config["__baseUrl"]');

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
