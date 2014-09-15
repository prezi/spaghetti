package prezi.graphics.text;

import prezi.graphics.core.Core;

class Layout {
	public static function createText(text:String = ""):Text {
		trace("Layout name: " + Spaghetti.getModuleName());
		trace("Static call: " + Core.giveMeANumber());
		var result = new TextImpl();
		result.insert(0, text, []);
		return result;
	}

	public static function createTestStuff():TestStuff<String, String>
	{
		return new TestStuffImpl<String, String>();
	}

	public static function createTestStuffWithStringKey<T>():TestStuff<T, String>
	{
		return new TestStuffImpl<T, String>();
	}

	public static function getResource():String
	{
		return Spaghetti.getResourceUrl("sample.txt");
	}
}
