package com.prezi.spaghetti.definition;

import com.prezi.spaghetti.bundle.DefinitionLanguage;

public interface ModuleDefinitionSource {
	String getLocation();

	String getContents();

	DefinitionLanguage getDefinitionLanguage();
}
