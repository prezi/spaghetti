package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.DEPENDENCIES
import static com.prezi.spaghetti.generator.ReservedWords.MODULE
import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class HaxeModuleAccessorGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {
	private final ModuleNode module

	HaxeModuleAccessorGeneratorVisitor(ModuleNode module) {
		this.module = module
	}

	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""@:final class ${node.alias} {

	static var __module:Dynamic = untyped __js__('${SPAGHETTI_CLASS}[\"${DEPENDENCIES}\"][\"${node.name}\"][\"${MODULE}\"]');

${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def paramNames = node.parameters*.name.join(", ")

		return \
"""	#if !spaghetti_noinline @:extern inline #end
	public static function ${node.name}${typeParams}(${params}):${returnType} {
		${node.returnType == VoidTypeReference.VOID ? "" : "return "}${module.alias}.__module.${node.name}(${paramNames});
	}
"""
	}
}
