package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.Annotation
import com.prezi.spaghetti.grammar.ModuleParser;

import groovy.json.StringEscapeUtils

public enum Type {
	Interface,
	Function,
	EnumName,
	EnumField,
	Constant,
	StructName,
	StructField
}


public class Deprecation {
	public static String annotation(Type type, String name, Annotation ann) {
		def typeName;
		switch (type) {
		case Type.Function: typeName = "function"
			break
		case Type.EnumName: typeName = "enum"
			break
		case Type.EnumField: typeName = "enum field"
			break
		case Type.Constant: typeName = "constant"
			break
		case Type.StructName: typeName = "struct"
			break
		case Type.StructField: typeName = "struct field"
			break
		case Type.Interface: typeName = "interface"
			break
		}

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