package com.prezi.spaghetti.json.ast;

class Interface extends ModuleType {
	var parameters:Array<String>;

	public function new(name:FQName, parameters:Array<String>) {
		super(name);
		this.parameters = parameters;
	}

	public function getParameters()
	{
		return parameters;
	}
}
