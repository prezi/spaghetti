package com.prezi.spaghetti.typescript

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

/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractTypeScriptGeneratorVisitor extends StringModuleVisitorBase {
	protected static final EnumMap<PrimitiveType, String> PRIMITIVE_TYPES = [
			(PrimitiveType.BOOL): "boolean",
			(PrimitiveType.INT): "number",
			(PrimitiveType.FLOAT): "number",
			(PrimitiveType.STRING): "string",
			(PrimitiveType.ANY): "any"
	]

	@Override
	String visitTypeChain(TypeChain node) {
		def parameters = node.parameters
		def retType = node.returnType.accept(this)

		if (parameters.empty) {
			return "() => ${retType}"
		}
		def params = []
		parameters.eachWithIndex { param, index ->
			params.push("arg${index}: ${param.accept(this)}")
		}
		return "(${params.join(", ")}) => ${retType}"
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
		if (TypeScriptGeneratorFactory.EXTERNS.containsKey(type)) {
			type = TypeScriptGeneratorFactory.EXTERNS.get(type)
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
		return "void"
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

		if (!doc) {
			return result
		}

		String prefix = (result =~ /^([ \t]*).*/)[0][1]
		def docResult = new StringBuilder(prefix).append("/**\n")
		doc.each { line ->
			docResult.append(prefix).append(" * ").append(line).append("\n")
		}
		docResult.append(prefix).append(" */\n")
		result = docResult + result
		return result
	}
}
