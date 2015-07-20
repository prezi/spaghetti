package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.MODULE

class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""export class ${node.alias} {

	private static ${MODULE}:any = ${GeneratorUtils.createModuleAccessor(node)};

${node.methods*.accept(new MethodVisitor(node)).join("")}
}
"""
	}

	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {

		private final ModuleNode module

		MethodVisitor(ModuleNode module) {
			this.module = module
		}

		@Override
		String visitMethodNode(MethodNode node) {
			def returnType = node.returnType.accept(this)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
			def params = node.parameters*.accept(this).join(", ")
			def paramNames = node.parameters*.name.join(", ")

			return \
"""	static ${node.name}${typeParams}(${params}):${returnType} {
		${returnType == "void" ? "" : "return "}${module.alias}.module.${node.name}(${paramNames});
	}
"""
		}
	}
}
