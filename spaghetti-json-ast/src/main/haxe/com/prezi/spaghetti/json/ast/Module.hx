package com.prezi.spaghetti.json.ast;

class Module extends Named
{
	var consts:FQNameMap<Const>;
	var types:FQNameMap<Type>;
	var enums:FQNameMap<Enum>;
	var interfaces:FQNameMap<Interface>;
	var structs:FQNameMap<Struct>;

	public function new(name:FQName)
	{
		super(name);
		this.consts = new FQNameMap();
		this.types = new FQNameMap();
		this.enums = new FQNameMap();
		this.interfaces = new FQNameMap();
		this.structs = new FQNameMap();
	}

	public function resolveType(name:FQName, arguments:Array<FQName>)
	{
		if (types.exists(name))
		{
			return ty
		}
	}

	public function getConst(name:FQName)
	{
		return consts.get(name);
	}
	public function registerConst(const:Const)
	{
		consts.set(const.name, const);
	}

	public function getEnum(name:FQName)
	{
		return enums.get(name);
	}
	public function registerEnum(en:Enum)
	{
		enums.set(en.name, en);
		registerType(en);
	}

	public function getInterface(name:FQName)
	{
		return interfaces.get(name);
	}
	public function registerInterface(iface:Interface)
	{
		interfaces.set(iface.name, iface);
		registerType(iface);
	}

	public function getStruct(name:FQName)
	{
		return structs.get(name);
	}
	public function registerStruct(struct:Struct)
	{
		structs.set(struct.name, struct);
		registerType(struct);
	}

	public function getType(name:FQName)
	{
		return types.get(name);
	}
	function registerType(type:Type)
	{
		types.set(type.name, type);
	}

	public function toString()
	{
		return types.toString();
	}
}
