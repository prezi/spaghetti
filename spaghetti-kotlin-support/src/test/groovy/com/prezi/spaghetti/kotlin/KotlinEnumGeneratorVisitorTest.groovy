package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.EnumParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser

class KotlinEnumGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	@deprecated("escape \\"this\\"!")
	BELA
	GEZA
}
"""
		def context = ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", definition)).parser.enumDefinition()
		def parser = new EnumParser(context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new KotlinEnumGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """class MyEnum {
	class object {
		/**
		 * Alma.
		 */
		val ALMA = MyEnum()
		[deprecated("escape \\"this\\"!")]
		val BELA = MyEnum()
		val GEZA = MyEnum()
		private val _values = arrayListOf(ALMA, BELA, GEZA)
		private val _names = arrayListOf("ALMA", "BELA", "GEZA")

		fun names():Array<String> = _names.copyToArray()

		fun values():Array<MyEnum> = _values.copyToArray()

		fun getName(value:MyEnum):String = _names.get(value as Int)

		fun getValue(value:MyEnum):Int = value as Int

		fun fromValue(value:Int):MyEnum {
			if (value < 0 || value >= _values.size) {
				throw IllegalArgumentException("Invalid value for MyEnum: " + value)
			}
			return _values[value]
		}

		fun valueOf(name:String):MyEnum {
			var result:MyEnum
			when(name)
			{
				"ALMA" -> result = ALMA
				"BELA" -> result = BELA
				"GEZA" -> result = GEZA
				else -> throw IllegalArgumentException("Invalid name for MyEnum: " + name)
			}
			return result
		}
	}
}
"""
	}
}
