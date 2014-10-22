package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode

final class TypeScriptUtils {
	public static File createSourceFile(String header, ModuleNode module, String name, File outputDirectory, String contents) {
		def namespace = module.name
		def packageDir = createNamespacePath(outputDirectory, namespace)
		packageDir.mkdirs()
		def file = new File(packageDir, name + ".ts")
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
