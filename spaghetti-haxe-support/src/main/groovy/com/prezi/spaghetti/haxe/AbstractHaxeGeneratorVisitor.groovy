package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.AnnotatedNode
import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentedNode
import com.prezi.spaghetti.ast.EnumReference
import com.prezi.spaghetti.ast.ExternReference
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.ast.StructReference
import com.prezi.spaghetti.ast.TypeChain
import com.prezi.spaghetti.ast.TypeParameterReference
import com.prezi.spaghetti.ast.VoidTypeReference
import groovy.json.StringEscapeUtils

abstract class AbstractHaxeGeneratorVisitor extends StringModuleVisitorBase {
	protected static final EnumMap<PrimitiveType, String> PRIMITIVE_TYPES = [
			(PrimitiveType.BOOL): "Bool",
			(PrimitiveType.INT): "Int",
			(PrimitiveType.FLOAT): "Float",
			(PrimitiveType.STRING): "String",
			(PrimitiveType.ANY): "Dynamic"
	]

	@Override
	String visitTypeChain(TypeChain node) {
		def result = node.elements*.accept(this).join("->")
		if (hasAncestor(TypeChain, 1)) {
			// This is a sub-type-chain
			result = "($result)"
		}
		return result
	}

	@Override
	String visitInterfaceReference(InterfaceReference reference) {
		def result = reference.type.qualifiedName.toString()
		if (!reference.arguments.empty) {
			result += "<" + reference.arguments*.accept(this).join(", ") + ">"
		}
		return wrapSingleTypeReference(result, reference.arrayDimensions);
	}

	@Override
	String visitStructReference(StructReference reference) {
		return wrapSingleTypeReference(reference.type.qualifiedName.toString(), reference.arrayDimensions)
	}

	@Override
	String visitEnumReference(EnumReference reference) {
		return wrapSingleTypeReference(reference.type.qualifiedName.toString(), reference.arrayDimensions)
	}

	@Override
	String visitTypeParameterReference(TypeParameterReference reference) {
		return wrapSingleTypeReference(reference.type.name.toString(), reference.arrayDimensions)
	}

	@Override
	String visitPrimitiveTypeReference(PrimitiveTypeReference reference) {
		String type = PRIMITIVE_TYPES.get(reference.type)
		return wrapSingleTypeReference(type, reference.arrayDimensions)
	}

	@Override
	String visitExternReference(ExternReference reference) {
		def type = reference.type.qualifiedName.toString()
		if (HaxeGeneratorFactory.EXTERNS.containsKey(type)) {
			type = HaxeGeneratorFactory.EXTERNS.get(type)
		}
		return wrapSingleTypeReference(type, reference.arrayDimensions)
	}

	static protected String wrapSingleTypeReference(String name, int arrayDimensions) {
		String result = name
		(0..<arrayDimensions).each {
			result = "Array<${result}>"
		}
		return result
	}

	@Override
	String visitVoidTypeReference(VoidTypeReference reference) {
		return "Void"
	}

	@Override
	String afterVisit(AstNode node, String result) {
		result = super.afterVisit(node, result)
		if (result) {
			result = decorateNode(node, result)
		}
		return result
	}

	@SuppressWarnings("GroovyAssignabilityCheck")
	private static String decorateNode(AstNode node, String result) {
		List<String> doc = node instanceof DocumentedNode ? node.documentation.documentation : []
		AnnotationNode deprecation = node instanceof AnnotatedNode ? node.annotations.find { it.name == "deprecated" } : null

		if (!doc && !deprecation) {
			return result
		}

		String prefix = (result =~ /^([ \t]*).*/)[0][1]

		if (deprecation) {
			def deprecationResult = new StringBuilder(prefix).append("@:deprecated")
			if (deprecation.hasDefaultParameter()) {
				def message = String.valueOf(deprecation.defaultParameter)
				message = StringEscapeUtils.escapeJava(message)
				deprecationResult.append("(\"").append(message).append("\")")
			}
			deprecationResult.append("\n")
			result = deprecationResult + result
		}

		if (doc) {
			def docResult = new StringBuilder(prefix).append("/**\n")
			doc.each { line ->
				docResult.append(prefix).append(" * ").append(line).append("\n")
			}
			docResult.append(prefix).append(" */\n")
			result = docResult + result
		}

		return result
	}
}
