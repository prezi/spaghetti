package com.prezi.spaghetti.definition

import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class DefinitionParserHelper extends Specification {

	public ModuleDefinition parse(String data) {
		def context = ModuleDefinitionParser.parse(new ModuleDefinitionSource(
				location: "test",
				contents: data))

		def scope = Mock(Scope)
		scope.resolveName(_) >> { name -> throw new IllegalStateException("global scope accessed: " + name) }
		scope.resolveExtern(_) >> { name -> name }
		return new ModuleDefinition("test", context, scope)
	}
}
