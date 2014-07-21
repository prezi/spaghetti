package prezi.graphics.text;

import prezi.graphics.core.Core;

class Layout implements ILayout {
	public function new(core:Core) {
		trace("Layout name: " + SpaghettiConfiguration.getName());
		trace("Static call: " + Core.giveMeANumber());
	}

	public function createText(text:String = ""):Text {
		var result = new TextImpl();
		result.insert(0, text, []);
		return result;
	}

	public function createTestStuff():TestStuff<String, String>
	{
		return new TestStuffImpl<String, String>();
	}

	public static function createTestStuffWithStringKey<T>():TestStuff<T, String>
	{
		return new TestStuffImpl<T, String>();
	}

	public function getResource():String
	{
		return SpaghettiConfiguration.getResourceUrl("sample.txt");
	}
}

