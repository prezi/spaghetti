package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC
import static com.prezi.spaghetti.typescript.TypeScriptGenerator.CREATE_MODULE_FUNCTION

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleInitializerGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	private final Collection<ModuleNode> dependencies

	TypeScriptModuleInitializerGeneratorVisitor(Collection<ModuleNode> dependencies)
	{
		this.dependencies = dependencies
	}

	@Override
	String visitModuleNode(ModuleNode node) {
		def instances = []

		dependencies.eachWithIndex { ModuleNode dependency, int index ->
			instances.add "var dependency${index}:${dependency.name}.${dependency.alias} = ${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${INSTANCE}\"];"
		}
		def references = (0..<instances.size()).collect { "dependency${it}" }
"""export function ${CREATE_MODULE_FUNCTION}():any {
	${instances.join("\n\t")}
	var module:${node.name}.I${node.alias} = new ${node.name}.${node.alias}(${references.join(", ")});
	var statics = new ${node.name}.__${node.alias}Static();
	return {
		${INSTANCE}: module,
		${STATIC}: statics
	}
}
"""
	}
}
