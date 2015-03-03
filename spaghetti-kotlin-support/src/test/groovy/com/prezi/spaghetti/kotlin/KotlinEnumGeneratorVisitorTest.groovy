package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class KotlinEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
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

		def result = parseAndVisitEnum(definition, new KotlinEnumGeneratorVisitor())

		expect:
		result == """class MyEnum {
	class object {
		/**
		 * Alma.
		 */
		val ALMA = 0 as MyEnum
		[deprecated("escape \\"this\\"!")]
		val BELA = 1 as MyEnum
		val GEZA = 2 as MyEnum
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
