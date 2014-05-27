package prezi.graphics.text;

import prezi.graphics.core.Core;

class Layout implements ILayout {
	public function new(core:Core) {
		trace("Layout name: " + SpaghettiConfiguration.getName());
		trace("Static call: " + Core.giveMeANumber());
	}

	public function createText():Text {
		return new TextImpl();
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

