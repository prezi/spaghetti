package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleMethodType
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC

class HaxeModuleAccessorGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""@:final class ${node.alias} {

	static var ${INSTANCE}:Dynamic = untyped __js__('${CONFIG}[\"${MODULES}\"][\"${node.name}\"][\"${INSTANCE}\"]');
	static var ${STATIC}:Dynamic = untyped __js__('${CONFIG}[\"${MODULES}\"][\"${node.name}\"][\"${STATIC}\"]');

${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitModuleMethodNode(ModuleMethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def paramNames = node.parameters*.name.join(", ")
		def delegate = node.type == ModuleMethodType.STATIC ? STATIC : INSTANCE

		return \
"""	@:extern public ${node.type == ModuleMethodType.STATIC ? "static " : ""}inline function ${node.name}${typeParams}(${params}):${returnType} {
		${node.returnType == VoidTypeReference.VOID ? "" : "return "}${delegate}.${node.name}(${paramNames});
	}
"""
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		def type = node.type.accept(this)
		type = wrapNullableTypeReference(type, node)
		return node.name + ":" + type
	}
}
