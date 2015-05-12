package com.prezi.spaghetti.ast;

/**
 * A location of a node in the original source.
 */
public interface Location {
	ModuleDefinitionSource getSource();

	int getLine();

	int getCharacter();
}
