package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.generator.internal.GeneratorUtils

final class HaxeUtils {
	public static File createHaxeSourceFile(String namespace, String name, File outputDirectory, String contents) {
		def packageDir = createNamespacePath(outputDirectory, namespace)
		packageDir.mkdirs()
		def file = new File(packageDir, name + ".hx")
		file.delete()
		file << "/*\n"
		file << " * " + GeneratorUtils.createHeaderComment() + "\n"
		file << " */\n"
		if (namespace)
		{
			file << "package ${namespace};\n"
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
