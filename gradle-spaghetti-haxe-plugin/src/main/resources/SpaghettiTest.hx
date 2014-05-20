class SpaghettiTest {
	public static function getConfig():SpaghettiModuleConfiguration {
		return untyped ${config};
	}

	public static function getModule():Dynamic {
		return untyped ${haxeModule}["${module}"];
	}
}
