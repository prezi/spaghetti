package com.prezi.gradle.spaghetti.parse

import org.gradle.api.Named

/**
 * Created by lptr on 14/11/13.
 */
class ParserContext {
	final Binding binding

	final Map<String, TypeDefinition> types = [:]
	ModuleDefinition module

	ParserContext(Binding binding)
	{
		this.binding = binding
		registerBinding(new TypeDefinition("void", this))
		registerBinding(new TypeDefinition("bool", this))
		registerBinding(new TypeDefinition("int", this))
		registerBinding(new TypeDefinition("float", this))
		registerBinding(new TypeDefinition("String", this))
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
		registerBinding(type)
		types.put(type.name, type)
	}

	void registerBinding(Named property) {
		if (binding.hasProperty(property.name)) {
			throw new IllegalStateException("Property ${property.name} is already registered")
		}
		binding.setProperty(property.name, property)

	}
}
