package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleMethodType
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC

class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
	private final ModuleNode module

	TypeScriptModuleAccessorGeneratorVisitor(ModuleNode module) {
		this.module = module
	}

	@Override
	String visitModuleNode(ModuleNode node) {
"""export class ${node.alias} {

	private static ${INSTANCE}:any = ${CONFIG}[\"${MODULES}\"][\"${node.name}\"][\"${INSTANCE}\"];
	private static ${STATIC}:any = ${CONFIG}[\"${MODULES}\"][\"${node.name}\"][\"${STATIC}\"];

${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitModuleMethodNode(ModuleMethodNode node) {
		def returnType = node.returnType.accept(this)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def paramNames = node.parameters*.name.join(", ")

		def isStatic = node.type == ModuleMethodType.STATIC
		def delegate = module.alias + "." + (isStatic ? STATIC : INSTANCE)

"""	${isStatic ? "static " : ""}${node.name}${typeParams}(${params}):${returnType} {
		${returnType == "void"?"":"return "}${delegate}.${node.name}(${paramNames});
	}
"""
	}
}
