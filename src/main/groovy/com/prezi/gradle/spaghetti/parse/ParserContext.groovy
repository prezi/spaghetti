package com.prezi.gradle.spaghetti.parse

import org.gradle.api.Named

/**
 * Created by lptr on 14/11/13.
 */
class ParserContext {
	final Binding binding

	final Map<String, TypeDefinition> types = [:]
	final Map<String, TypeDefinition> builtInTypes = [:]
	ModuleDefinition module

	ParserContext(Binding binding)
	{
		this.binding = binding
		[
			new TypeDefinition("aVoid", this),
			new TypeDefinition("aBool", this),
			new TypeDefinition("aInt", this),
			new TypeDefinition("aFloat", this),
			new TypeDefinition("aString", this)
		].each {
			builtInTypes.put(it.name, it)
		}
	}

	ModuleDefinition setModule() {
		if (module != null) {
			throw new IllegalStateException("A module with name ${module.name} is already defined")
		}
		this.module = module
	}

	void registerType(TypeDefinition type) {
		if (types.containsKey(type.name)) {
			throw new IllegalStateException("Type ${type.name} is already registered")
		}
		if (builtInTypes.containsKey(type.name)) {
			throw new IllegalStateException("Type ${type.name} is a built-in type")
		}
		types.put(type.name, type)
	}

	TypeDefinition getType(String name) {
		if (types.containsKey(name))
		{
			return types.get(name)
		}
		if (builtInTypes.containsKey(name))
		{
			return builtInTypes.get(name)
		}
		throw new IllegalStateException("Type ${name} is not registered")
	}
}
