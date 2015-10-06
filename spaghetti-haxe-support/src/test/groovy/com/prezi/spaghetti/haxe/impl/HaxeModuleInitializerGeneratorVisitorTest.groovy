package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class HaxeModuleInitializerGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test
doStatic(x: int): int;
"""
		def result = parseAndVisitModule(definition, new HaxeModuleInitializerGeneratorVisitor())

		expect:
		result == """@:keep class __TestModuleInit {
	public static var delayedInitFinished = delayedInit();
	static function delayedInit():Bool {
		untyped __haxeModule = new com.example.test.__TestModuleProxy();
		return true;
	}
}
"""
	}
}
