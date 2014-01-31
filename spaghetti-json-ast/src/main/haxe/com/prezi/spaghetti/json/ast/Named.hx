package com.prezi.spaghetti.json.ast;

class Named {
	var name:FQName;

	public function new(name:FQName) {
		this.name = name;
	}

	public function getName()
	{
		return name;
	}
}
