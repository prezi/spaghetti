package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode

final class TypeScriptUtils {
	public static File createSourceFile(String header, ModuleNode module, String name, File outputDirectory, String contents) {
		def namespace = module.name
		def file = new File(outputDirectory, name + ".ts")
		file.delete()
		file << "/*\n"
		file << " * " + header + "\n"
		file << " */\n"
		if (namespace)
		{
			file << "module ${namespace} {\n"
			file << contents
			file << "}"
		}
		else {
			file << contents
		}
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
