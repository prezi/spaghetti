package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.DEPENDENCIES
import static com.prezi.spaghetti.generator.ReservedWords.MODULE
import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
	private final ModuleNode module

	TypeScriptModuleAccessorGeneratorVisitor(ModuleNode module) {
		this.module = module
	}

	@Override
	String visitModuleNode(ModuleNode node) {
"""export class ${node.alias} {

	private static ${MODULE}:any = ${SPAGHETTI_CLASS}[\"${DEPENDENCIES}\"][\"${node.name}\"][\"${MODULE}\"];

${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def paramNames = node.parameters*.name.join(", ")

"""	static ${node.name}${typeParams}(${params}):${returnType} {
		${returnType == "void"?"":"return "}${module.alias}.module.${node.name}(${paramNames});
	}
"""
	}
}
