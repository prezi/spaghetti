package com.prezi.spaghetti.definition;

import java.io.File;
import java.io.Serializable;

public interface DefinitionFile extends Serializable {
	File getFile();
	String getNamespaceOverride();
}
