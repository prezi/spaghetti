package com.prezi.spaghetti.kotlin.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.kotlin.AbstractKotlinMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.DEPENDENCIES
import static com.prezi.spaghetti.generator.ReservedWords.MODULE
import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class KotlinModuleAccessorGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {
	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""native("${SPAGHETTI_CLASS}[\\"${DEPENDENCIES}\\"][\\"${node.name}\\"][\\"${MODULE}\\"]") val moduleRef:${node.alias} = noImpl

object ${node.alias} {
	val module:${node.alias} = moduleRef;

${node.methods*.accept(new MethodVisitor(node)).join("")}
}
"""
	}

	private static class MethodVisitor extends AbstractKotlinMethodGeneratorVisitor {
		private final ModuleNode module

		MethodVisitor(ModuleNode module) {
			this.module = module
		}

		@Override
		String visitMethodNode(MethodNode node) {
			def returnType = node.returnType.accept(this)
			returnType = wrapNullableTypeReference(returnType, node)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + "> " : ""
			def params = node.parameters*.accept(this).join(", ")
			def paramNames = node.parameters*.name.join(", ")

			return \
"""	fun ${typeParams}${node.name}(${params}):${returnType} {
		${returnType == "Void"?"":"return "}module.${node.name}(${paramNames})
	}
"""
		}
	}
}
