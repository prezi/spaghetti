package prezi.graphics.text;

import prezi.graphics.core.Core;

class SpaghettiModuleImpl implements SpaghettiModule {
	private var config:SpaghettiModuleConfiguration;

	public function new(config:SpaghettiModuleConfiguration) {
		this.config = config;
		trace("Layout name: " + config.getName());
		trace("Static call: " + Core.giveMeANumber());
	}

	public function createText():Text {
		return new TextImpl();
	}

	public function createTestStuff():TestStuff<String, String>
	{
		return new TestStuffImpl<String, String>();
	}

	public function getResource():String
	{
		return config.getResourceUrl("sample.txt");
	}
}

