package com.prezi.spaghetti.json.ast;

class Struct extends ModuleType {
	var properties:Map<String, Property>;

	public function new(name:FQName) {
		super(name);
		this.properties = new Map();
	}

	public function registerProperty(name:String, type:TypeReference<Dynamic>)
	{
		properties.put(new Property(name, type));
	}
}
