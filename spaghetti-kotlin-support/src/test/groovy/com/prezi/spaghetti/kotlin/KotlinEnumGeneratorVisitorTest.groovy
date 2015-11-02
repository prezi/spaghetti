package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.EnumGeneratorSpecification

class KotlinEnumGeneratorVisitorTest extends EnumGeneratorSpecification {
	def "generate implicit"() {
		def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA,
	@deprecated("escape \\"this\\"!")
	BELA,
	GEZA
}
"""

		def result = parseAndVisitEnum(definition, new KotlinEnumGeneratorVisitor())

		expect:
		result == """class MyEnum {
	companion object {
		/**
		 * Alma.
		 */
		val ALMA = 0 as MyEnum
		[deprecated("escape \\"this\\"!")]
		val BELA = 1 as MyEnum
		val GEZA = 2 as MyEnum
		private val _values = hashMapOf(ALMA.toString() to ALMA, BELA.toString() to BELA, GEZA.toString() to GEZA)
		private val _names = hashMapOf(ALMA.toString() to "ALMA", BELA.toString() to "BELA", GEZA.toString() to "GEZA")

		fun names():Array<String> = arrayOf("ALMA", "BELA", "GEZA")

		fun values():Array<MyEnum> = arrayOf(ALMA, BELA, GEZA)

		fun getName(value:MyEnum):String = _names.get((value as Int).toString()) ?: throw IllegalArgumentException("Invalid value for MyEnum: " + value)

		fun getValue(value:MyEnum):Int = value as Int

		fun fromValue(value:Int):MyEnum = _values.get(value.toString()) ?: throw IllegalArgumentException("Invalid value for MyEnum: " + value)

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

	def "generate explicit"() {
		def definition = """enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 1,
	@deprecated("escape \\"this\\"!")
	BELA = 2,
	GEZA = 4
}
"""

		def result = parseAndVisitEnum(definition, new KotlinEnumGeneratorVisitor())

		expect:
		result == """class MyEnum {
	companion object {
		/**
		 * Alma.
		 */
		val ALMA = 1 as MyEnum
		[deprecated("escape \\"this\\"!")]
		val BELA = 2 as MyEnum
		val GEZA = 4 as MyEnum
		private val _values = hashMapOf(ALMA.toString() to ALMA, BELA.toString() to BELA, GEZA.toString() to GEZA)
		private val _names = hashMapOf(ALMA.toString() to "ALMA", BELA.toString() to "BELA", GEZA.toString() to "GEZA")

		fun names():Array<String> = arrayOf("ALMA", "BELA", "GEZA")

		fun values():Array<MyEnum> = arrayOf(ALMA, BELA, GEZA)

		fun getName(value:MyEnum):String = _names.get((value as Int).toString()) ?: throw IllegalArgumentException("Invalid value for MyEnum: " + value)

		fun getValue(value:MyEnum):Int = value as Int

		fun fromValue(value:Int):MyEnum = _values.get(value.toString()) ?: throw IllegalArgumentException("Invalid value for MyEnum: " + value)

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
