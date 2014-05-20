class SpaghettiTest {
	public static function getConfig():SpaghettiModuleConfiguration {
		return untyped ${config};
	}

	public static function getModule(name:String = null):Dynamic {
		if (name == null) {
			return untyped ${haxeModule}["${module}"];
		} else {
			return untyped ${config}["${modules}"][name]["${module}"];
		}
	}
}
