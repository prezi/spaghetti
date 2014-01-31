package com.prezi.spaghetti.json.ast;

@:final class FQName
{
	var namespace:String;
	var localName:String;

	function new(namespace:String, localName:String)
	{
		this.namespace = namespace;
		this.localName = localName;
	}

	public static function parse(name:String)
	{
		var localName;
		var namespace;
		var lastDot = name.lastIndexOf(".");
		if (lastDot == -1)
		{
			namespace = null;
			localName = name;
		} else
		{
			namespace = name.substring(0, lastDot);
			localName = name.substring(lastDot + 1);
		}
		return new FQName(namespace, localName);
	}

	public function qualifyLocalName(name:FQName)
	{
		if (name.hasNamespace())
		{
			return name;
		} else
		{
			return new FQName(namespace, name.localName);
		}
	}

	public inline function getLocalName()
	{
		return localName;
	}

	public inline function getFullyQualifiedName()
	{
		return getNamespacePrefix() + localName;
	}

	public inline function getNamespacePrefix()
	{
		return (namespace == null ? "" : namespace + ".");
	}

	public inline function getNamespace()
	{
		return namespace;
	}

	public inline function hasNamespace()
	{
		return namespace != null;
	}

	public function toString()
	{
		return getFullyQualifiedName();
	}
}
