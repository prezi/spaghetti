package com.prezi.spaghetti.ast.internal.parser;

import java.io.Reader;

public interface ModuleSource {
	String getDescription();
	Reader getData();
}
