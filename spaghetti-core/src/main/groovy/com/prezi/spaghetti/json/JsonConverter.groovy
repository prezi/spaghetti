package com.prezi.spaghetti.json

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import groovy.json.JsonOutput
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 28/01/14.
 */
class JsonConverter {
	public static String toJson(ModuleConfiguration config) {
		def modules = config.modules.values().collectEntries() { [ it.name.fullyQualifiedName, toJsonMap(it) ] }
		JsonOutput.toJson(modules: modules)
	}

	public static Map<String, Object> toJsonMap(ModuleDefinition module) {
		return new JsonVisitor(module).processModule() as Map<String, Object>
	}
}
