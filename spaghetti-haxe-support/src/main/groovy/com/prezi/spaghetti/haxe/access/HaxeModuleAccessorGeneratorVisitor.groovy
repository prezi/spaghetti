package com.prezi.spaghetti.haxe.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor
import com.prezi.spaghetti.generator.GeneratorUtils

class HaxeModuleAccessorGeneratorVisitor extends AbstractHaxeGeneratorVisitor {
	private ModuleFormat format;

	public HaxeModuleAccessorGeneratorVisitor(ModuleFormat format) {
		this.format = format;
	}

	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""@:final class ${node.alias} {

	static var __module:Dynamic = untyped __js__('${GeneratorUtils.createModuleAccessor(node.name, format)}');

${node.methods*.accept(new MethodVisitor(node)).join("")}
}
"""
	}

	private static class MethodVisitor extends AbstractHaxeMethodGeneratorVisitor {
		private final ModuleNode module

		MethodVisitor(ModuleNode module) {
			this.module = module
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
		${node.returnType instanceof VoidTypeReference ? "" : "return "}${module.alias}.__module.${node.name}(${paramNames});
	}
"""
		}
	}
}
