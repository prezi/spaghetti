package com.prezi.spaghetti.json.ast;

class TypeReference<T:Type>
{
	var type:T;

	function new(type:T)
	{
		this.type = type;
	}

	public function getType():T
	{
		return type;
	}
}

class PrimitiveReference extends TypeReference<PrimitiveType>
{
	public function new(type:PrimitiveType)
	{
		super(type);
	}
}

class StructReference extends TypeReference<Struct>
{
	public function new(type:Struct)
	{
		super(type);
	}
}

class EnumReference extends TypeReference<Enum>
{
	public function new(type:Enum)
	{
		super(type);
	}
}

class InterfaceReference extends TypeReference<Interface>
{
	var parameters:Map<String, TypeReference<Dynamic>>;

	public function new(type:Interface, parameters:Map<String, TypeReference<Dynamic>>)
	{
		super(type);
		this.parameters = parameters;
	}

	public function getParameters()
	{
		return parameters;
	}
}
