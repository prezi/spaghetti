package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AnnotatedNode
import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentedNode
import com.prezi.spaghetti.ast.EnumReference
import com.prezi.spaghetti.ast.ExternInterfaceReference
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode
import com.prezi.spaghetti.ast.ParametrizedTypeNodeReference
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.ast.StructReference
import com.prezi.spaghetti.ast.TypeChain
import com.prezi.spaghetti.ast.TypeParameterReference
import com.prezi.spaghetti.ast.VoidTypeReference
import org.apache.commons.lang.StringEscapeUtils

abstract class AbstractKotlinGeneratorVisitor extends StringModuleVisitorBase {
	private static def EXTERNS = [:].asImmutable()

	protected static final EnumMap<PrimitiveType, String> PRIMITIVE_TYPES = [
			(PrimitiveType.BOOL): "Boolean",
			(PrimitiveType.INT): "Int",
			(PrimitiveType.FLOAT): "Double",
			(PrimitiveType.STRING): "String",
			(PrimitiveType.ANY): "Any"
	]

	@Override
	String visitTypeChain(TypeChain node) {
		def parameters = node.parameters
		def retType = node.returnType.accept(this)
		return "(${parameters*.accept(this).join(",")})->${retType}"
	}

	@Override
	String visitInterfaceReference(InterfaceReference reference) {
		def type = reference.type.qualifiedName.toString()
		return wrapParametrizedTypeReference(type, reference)
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
	String visitExternInterfaceReference(ExternInterfaceReference reference) {
		def type = reference.type.qualifiedName.toString()
		if (EXTERNS.containsKey(type)) {
			type = EXTERNS.get(type)
		}
		return wrapParametrizedTypeReference(type, reference)
	}

	private String wrapParametrizedTypeReference(String type, ParametrizedTypeNodeReference<? extends ParametrizedReferableTypeNode> reference) {
		def result = type
		if (!reference.arguments.empty) {
			result += "<" + reference.arguments*.accept(this).join(", ") + ">"
		}
		return wrapSingleTypeReference(result, reference.arrayDimensions)
	}

	static protected String wrapNullableTypeReference(String name, AnnotatedNode annotated) {
		return wrapNullableTypeReference(name, annotated.annotations.contains("nullable"))
	}

	static protected String wrapNullableTypeReference(String name, boolean nullable) {
		return nullable ? name + "?" : name
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
		return "Unit"
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
			def deprecationResult = new StringBuilder(prefix).append("[deprecated")
			if (deprecation.hasDefaultParameter()) {
				def message = String.valueOf(deprecation.defaultParameter)
				message = StringEscapeUtils.escapeJava(message)
				deprecationResult.append("(\"").append(message).append("\")")
			}
			deprecationResult.append("]\n")
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
