package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class KotlinEnumGeneratorVisitor extends AbstractKotlinGeneratorVisitor {
	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name

		def values = []
		node.values.eachWithIndex { value, index ->
			values.add value.accept(new EnumValueVisitor(enumName, index))
		}

		return \
"""class ${enumName} {
	class object {
${values.join("\n")}
		private val _values = arrayListOf(${node.values.join(", ")})
		private val _names = arrayListOf(${node.values.collect { "\"${it}\"" }.join(", ")})

		fun names():Array<String> = _names.copyToArray()

		fun values():Array<${enumName}> = _values.copyToArray()

		fun getName(value:${enumName}):String = _names.get(value as Int)

		fun getValue(value:${enumName}):Int = value as Int

		fun fromValue(value:Int):${enumName} {
			if (value < 0 || value >= _values.size) {
				throw IllegalArgumentException("Invalid value for ${enumName}: " + value)
			}
			return _values[value]
		}

		fun valueOf(name:String):${enumName} {
			var result:${enumName}
			when(name)
			{
${node.values.collect { "				\"${it}\" -> result = ${it}" }.join("\n")}
				else -> throw IllegalArgumentException("Invalid name for ${enumName}: " + name)
			}
			return result
		}
	}
}
"""
	}

	private static class EnumValueVisitor extends AbstractKotlinGeneratorVisitor {
		private final String enumName
		private final int index

		EnumValueVisitor(String enumName, int index) {
			this.enumName = enumName
			this.index = index
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "		val ${node.name} = ${enumName}()"
		}
	}
}
