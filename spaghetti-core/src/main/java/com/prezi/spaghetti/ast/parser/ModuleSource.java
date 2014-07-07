package com.prezi.spaghetti.ast.parser;

import java.io.Reader;

public interface ModuleSource {
	String getDescription();
	Reader getData();
}
