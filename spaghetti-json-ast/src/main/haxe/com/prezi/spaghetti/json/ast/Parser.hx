package com.prezi.spaghetti.json.ast;

import haxe.Json;

class Parser
{
	public static function parse(data:String)
	{
		// trace(data);
		var _module = Json.parse(data);
		var moduleName = FQName.parse(_module.name);
		var module = new Module(moduleName);

		// Phase #1: Parse types
		for (_const in cast(_module.consts, Array<Dynamic>))
		{
			module.registerConst(new Const(qualify(moduleName, _const)));
		}
		for (_enum in cast(_module.enums, Array<Dynamic>))
		{
			module.registerEnum(new Enum(qualify(moduleName, _enum)));
		}
		for (_iface in cast(_module.interfaces, Array<Dynamic>))
		{
			module.registerInterface(new Interface(qualify(moduleName, _iface), _iface.parameters));
		}
		for (_struct in cast(_module.structs, Array<Dynamic>))
		{
			module.registerStruct(new Struct(qualify(moduleName, _struct)));
		}

		// Phase #2: Parse internals of types
		for (_iface in cast(_module.interfaces, Array<Dynamic>))
		{
			var iface = module.getInterface(qualify(moduleName, _iface));

		}
		for (_struct in cast(_module.structs, Array<Dynamic>))
		{
			var struct = module.getStruct(qualify(moduleName, _struct));
			for (_property in cast(_struct.properties, Array<Dynamic>))
			{
				var name = _property.name;
				var type = parseType(module, _property.type);
				struct.registerProperty(name, type);
			}
		}
		trace('Module: ${module}');
	}

	static function parseType(module:Module, _type:String)
	{

	}

	static function qualify(moduleName:FQName, _named:Dynamic)
	{
		var localName = FQName.parse(_named.name);
		var fqName = moduleName.qualifyLocalName(localName);
		return fqName;
	}
}
