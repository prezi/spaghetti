package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC
import static com.prezi.spaghetti.haxe.HaxeGenerator.HAXE_MODULE_VAR

class HaxeModuleInitializerGeneratorVisitor extends StringModuleVisitorBase {

	private final Collection<ModuleNode> dependencies

	HaxeModuleInitializerGeneratorVisitor(Collection<ModuleNode> dependencies)
	{
		this.dependencies = dependencies
	}

	@Override
	String visitModuleNode(ModuleNode node) {
		def initializerName = "__" + node.alias + "Init"

		def instances = []
		dependencies.eachWithIndex { ModuleNode dependency, int index ->
			instances.add "var dependency${index}:${dependency.name}.${dependency.alias} = untyped __js__('${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${INSTANCE}\"]');"
		}
		def references = (0..<instances.size()).collect { "dependency${it}" }

		def initializerContents =
"""@:keep class ${initializerName} {
#if (js && !test)
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		${instances.join("\n\t\t")}
		var module:${node.name}.I${node.alias} = new ${node.name}.${node.alias}(${references.join(", ")});
		var statics = new ${node.name}.__${node.alias}Static();
		untyped ${HAXE_MODULE_VAR} = {
			${INSTANCE}: module,
			${STATIC}: statics
		}
		return true;
	}
#end
}
"""
		return initializerContents
	}
}
