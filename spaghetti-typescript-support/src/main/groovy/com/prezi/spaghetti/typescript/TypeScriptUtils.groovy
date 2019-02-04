package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode

final class TypeScriptUtils {
	public static File createSourceFile(String header, String name, File outputDirectory, String contents) {
		File file = new File(outputDirectory, name);
		file.delete()
		if (header != null && header.length() > 0) {
			file << "/*\n"
			file << " * " + header + "\n"
			file << " */\n"
		}
		file << contents
		return file
	}

	public static String toPrimitiveString(Object value) {
		if (value instanceof String) {
			return '"' + value + '"'
		} else {
			return String.valueOf(value)
		}
	}
}
