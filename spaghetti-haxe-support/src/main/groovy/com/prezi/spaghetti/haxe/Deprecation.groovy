package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.Annotation
import com.prezi.spaghetti.definition.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import groovy.json.StringEscapeUtils


public class Deprecation {

	public static String formatDeprecationWithAutoPrefix(ModuleParser.AnnotationsContext ctx, String text)
	{
		def m = text =~ /^([ \t]*).*/
		return formatDeprecation(ctx, m[0][1]) + text
	}

	public static String formatDeprecation(ModuleParser.AnnotationsContext ctx, String prefix) {
		def deprecation = annotationFromCxt(ctx)
		if (deprecation) {
			deprecation = prefix + deprecation
		}
		return deprecation
	}

	public static String annotationFromCxt(ModuleParser.AnnotationsContext ctx) {
		def deprecatedAnn = ModuleUtils.extractAnnotations(ctx)["deprecated"]
		if (deprecatedAnn != null) {
			return annotation(deprecatedAnn) + "\n"
		} else {
			return "";
		}
	}

	public static String annotation(Annotation ann) {
		def deprecationMessage;
		if (ann.hasDefaultParameter()) {
			deprecationMessage = "(\"" + StringEscapeUtils.escapeJava(String.valueOf(ann.getDefaultParameter())) + "\")"
		} else {
			deprecationMessage = ""
		}

		return "@:deprecated${deprecationMessage}"
	}
}
