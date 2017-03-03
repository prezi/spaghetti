package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode

final class TypeScriptUtils {
	public static File createSourceFile(String header, ModuleNode module, String name, File outputDirectory, String contents) {
		def namespace = module.name
		if (namespace)
		{
			def namespaceContent = "module ${namespace} {\n"
			namespaceContent += contents
			namespaceContent += "}"

			def packageDir = createNamespacePath(outputDirectory, namespace)
			packageDir.mkdirs()

			return createRawSourceFile(header, module, name, packageDir, namespaceContent);
		} else {
			return createRawSourceFile(header, module, name, outputDirectory, contents);
		}
	}

	public static File createRawSourceFile(String header, ModuleNode module, String name, File outputDirectory, String contents) {
		def file = new File(outputDirectory, name + ".ts")
		file.delete()
		file << "/*\n"
		file << " * " + header + "\n"
		file << " */\n"
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
