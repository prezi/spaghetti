package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleParser.AnnotationsContext
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

	public static String formatDocumentationWithAutoPrefix(Token doc, String text)
	{
		def m = text =~ /^([ \t]*).*/
		return formatDocumentation(doc, m[0][1]) + text
	}

	public static Map<String, Annotation> extractAnnotations(AnnotationsContext context) {
		return context?.annotation()?.collectEntries { annotationCtx ->
			def annotation = Annotation.fromContext(annotationCtx)
			return [ annotation.name, annotation ]
		}?.asImmutable() ?: Collections.emptyMap()
	}
}
