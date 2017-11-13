package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode

final class TypeScriptUtils {
	public static File createSourceFile(String header, ModuleNode module, String name, File outputDirectory, String contents, String postNamespaceContent) {
		def namespace = module.name
		if (namespace)
		{
			def namespaceContent = "module ${namespace} {\n"
			namespaceContent += contents
			namespaceContent += "}"
			if (postNamespaceContent != null) {
				namespaceContent += postNamespaceContent
			}

			def packageDir = createNamespacePath(outputDirectory, namespace)
			packageDir.mkdirs()

			return createRawSourceFile(header, name, packageDir, namespaceContent);
		} else {
			return createRawSourceFile(header, name, outputDirectory, contents);
		}
	}

	public static File createRawSourceFile(String header, String name, File outputDirectory, String contents) {
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
