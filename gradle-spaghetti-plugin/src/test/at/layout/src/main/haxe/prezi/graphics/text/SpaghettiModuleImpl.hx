package prezi.graphics.text;

class SpaghettiModuleImpl implements SpaghettiModule {
	private var config:SpaghettiModuleConfiguration;

	public function new(config:SpaghettiModuleConfiguration) {
		this.config = config;
		trace("Layout name: " + config.getName());
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

