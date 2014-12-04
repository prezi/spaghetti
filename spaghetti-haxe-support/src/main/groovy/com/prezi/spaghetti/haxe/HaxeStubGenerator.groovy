package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.generator.AbstractStubGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.haxe.stub.HaxeInterfaceStubGeneratorVisitor

class HaxeStubGenerator extends AbstractStubGenerator {
	HaxeStubGenerator() {
		super("haxe")
	}

	@Override
	void generateStubs(GeneratorParameters params, File outputDirectory) throws IOException {
		def config = params.moduleConfiguration
		def header = params.header
		config.allModules.each { module ->
			for (type in module.types) {
				if (type instanceof InterfaceNode) {
					def contents = new HaxeInterfaceStubGeneratorVisitor().visit(type)
					HaxeUtils.createHaxeSourceFile(header, module.name, type.name + "Stub", outputDirectory, contents)
				}
			}
		}
	}
}
