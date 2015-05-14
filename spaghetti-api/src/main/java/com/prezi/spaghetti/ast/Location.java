package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;

/**
 * A location of a node in the original source.
 */
public interface Location {
	ModuleDefinitionSource getSource();

	int getLine();

	int getCharacter();
}
