class SpaghettiTest {
	public static function getModule(name:String = null):Dynamic {
		if (name == null) {
			return untyped __js__('${haxeModule}["${module}"]');
		} else {
			return untyped __js__('${config}["${modules}"][name]["${module}"]');
		}
	}
}
