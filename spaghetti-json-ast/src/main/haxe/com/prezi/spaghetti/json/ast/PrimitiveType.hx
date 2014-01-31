package com.prezi.spaghetti.json.ast;

@:final class PrimitiveType extends Type {

	public static var Void (default, null) = new PrimitiveType(FQName.parse("void"));
	public static var Bool (default, null) = new PrimitiveType(FQName.parse("bool"));
	public static var Int (default, null) = new PrimitiveType(FQName.parse("int"));
	public static var Float (default, null) = new PrimitiveType(FQName.parse("float"));
	public static var String (default, null) = new PrimitiveType(FQName.parse("string"));

	function new(name:FQName) {
		super(name);
	}
}
