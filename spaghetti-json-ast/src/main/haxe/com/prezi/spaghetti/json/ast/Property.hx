package com.prezi.spaghetti.json.ast;

class Property {

	var name:String;
	var type:TypeReference<Dynamic>;

	public function new(name:String, type:TypeReference<Dynamic>)
	{
		this.name = name;
		this.type = type;
	}

	public function getName()
	{
		return name;
	}

	public function getType()
	{
		return type;
	}
}
