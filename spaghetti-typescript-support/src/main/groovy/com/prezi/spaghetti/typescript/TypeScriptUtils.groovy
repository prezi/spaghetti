package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.internal.GeneratorUtils

final class TypeScriptUtils {
	public static File createSourceFile(ModuleNode module, String name, File outputDirectory, String contents) {
		def namespace = module.name
		def file = new File(outputDirectory, name + ".ts")
		file.delete()
		file << "/*\n"
		file << " * " + GeneratorUtils.createHeaderComment() + "\n"
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
