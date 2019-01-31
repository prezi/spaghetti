package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode

final class TypeScriptUtils {
	public static File createSourceFile(String header, String name, File outputDirectory, String contents) {
		return createRawSourceFile(header, contents, new File(outputDirectory, name + ".ts"));
	}

	public static File createRawSourceFile(String header, String contents, File file) {
		file.delete()
		if (header != null && header.length() > 0) {
			file << "/*\n"
			file << " * " + header + "\n"
			file << " */\n"
		}
		file << contents
		return file
	}

	private static File createNamespacePath(File root, String namespace) {
		File result = root
		if (namespace) {
			namespace.split(/\./).each { name ->
				result = new File(result, name)
			}
		}
		return result
	}

	public static String toPrimitiveString(Object value) {
		if (value instanceof String) {
			return '"' + value + '"'
		} else {
			return String.valueOf(value)
		}
	}
}
