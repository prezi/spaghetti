package com.prezi.spaghetti.json.ast;

import Map;

@:final class FQNameMap<V> implements IMap<FQName, V>
{
	var delegate:Map<String, V>;

	public function new()
	{
		this.delegate = new Map();
	}

	public function exists(key:FQName):Bool
	{
		return delegate.exists(key.getFullyQualifiedName());
	}

	public function get(key:FQName):Null<V>
	{
		return delegate.get(key.getFullyQualifiedName());
	}

	public function iterator():Iterator<V>
	{
		return delegate.iterator();
	}

	public function keys():Iterator<FQName>
	{
		return new FQNameItr(delegate.keys());
	}

	public function remove(key:FQName):Bool
	{
		return delegate.remove(key.getFullyQualifiedName());
	}

	public function set(key:FQName, value:V):Void
	{
		delegate.set(key.getFullyQualifiedName(), value);
	}

	public function toString():String
	{
		return delegate.toString();
	}
}

class FQNameItr {
	var delegate:Iterator<String>;

	public function new(delegate:Iterator<String>)
	{
		this.delegate = delegate;
	}

	public function next():FQName
	{
		return FQName.parse(delegate.next());
	}

	public function hasNext():Bool
	{
		return delegate.hasNext();
	}
}
