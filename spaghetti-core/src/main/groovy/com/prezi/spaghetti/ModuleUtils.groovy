package com.prezi.spaghetti

import org.antlr.v4.runtime.Token

/**
 * Created by lptr on 21/11/13.
 */
final class ModuleUtils {
	public static String formatDocumentation(Token doc, String prefix = "")
	{
		if (doc == null || doc.text == null || doc.text == "") {
			return ""
		}
		List<String> lines = doc.text.split(/\n/)
		def result = prefix + lines.remove(0) + "\n"
		lines.each { line ->
			result += prefix + line.replaceFirst(/^\s+ \*/, " *") + "\n"
		}
		return "\n" + result
	}
}
