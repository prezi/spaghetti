package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.Annotation
import com.prezi.spaghetti.grammar.ModuleParser;
import com.prezi.spaghetti.ModuleUtils

import groovy.json.StringEscapeUtils

public enum Type {
	Interface,
	Function,
	EnumName,
	EnumField,
	ConstantName,
	ConstantField,
	StructName,
	StructField
}


public class Deprecation {

	private static Map<Type, String> typeNameMap = [
		(Type.Function) : "function",
		(Type.EnumName) : "enum",
		(Type.EnumField) : "enum field",
		(Type.ConstantName) : "constant",
		(Type.ConstantField) : "constant field",
		(Type.StructName) : "struct",
		(Type.StructField) : "struct field",
		(Type.Interface) : "interface",
	];

	public static String annotationFromCxt(Type type, String name, ModuleParser.AnnotationsContext ctx) {
		def deprecatedAnn = ModuleUtils.extractAnnotations(ctx)["deprecated"]
		if (deprecatedAnn != null) {
			return annotation(type, name, deprecatedAnn) + "\n"
		} else {
			return "";
		}
	}

	public static String annotation(Type type, String name, Annotation ann) {
		def typeName = typeNameMap[type];

		def deprecationMessage;
		if (ann.hasParameter("default")) {
			String hey = ann.getParameter('default');
			deprecationMessage = "Deprecated ${typeName} \\\"${name}\\\": " + StringEscapeUtils.escapeJava(ann.getParameter("default"))
		} else {
			deprecationMessage = "Deprecated ${typeName} \\\"${name}\\\""
		}

		return "@:deprecated(\"${deprecationMessage}\")"
	}
}