package prezi.graphics.text;

@:final
class Values {
	public static var HELLO (default, never):Int = 12;
	public static var HI (default, never):String = "Hello";
	public static var defaultStyle = {
		type: CharacterStyleType.COLOR,
		value: 0x123456
	};
}
