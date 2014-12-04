package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.generator.AbstractStubGenerator
import com.prezi.spaghetti.generator.GeneratorParameters

class KotlinStubGenerator extends AbstractStubGenerator {

	KotlinStubGenerator() {
		super("kotlin")
	}

	@Override
	void generateStubs(GeneratorParameters params, File outputDirectory) throws IOException {
		def config = params.moduleConfiguration
		def header = params.header
		config.allModules.each { module ->
			for (type in module.types) {
				if (type instanceof InterfaceNode) {
					def contents = "" // new KotlinInterfaceStubGeneratorVisitor().visit(type)
					KotlinUtils.createKotlinSourceFile(header, module.name, type.name + "Stub", outputDirectory, contents)
				}
			}
		}
	}
}
