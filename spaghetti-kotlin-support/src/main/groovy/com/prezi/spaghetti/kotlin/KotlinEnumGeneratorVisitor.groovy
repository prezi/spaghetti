package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.generator.EnumGeneratorUtils

class KotlinEnumGeneratorVisitor extends AbstractKotlinGeneratorVisitor {
	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name

		def namesToValues = EnumGeneratorUtils.calculateEnumValues(node)
		def valueVisitor = new EnumValueVisitor(enumName, namesToValues)
		def values = []
		node.values.each { value ->
			values.add value.accept(valueVisitor)
		}

		return \
"""class ${enumName} {
	class object {
${values.join("\n")}
		private val _values = hashMap(${namesToValues.collect { name, value -> "\"${value}\" to ${name}"}.join(", ")})
		private val _names = hashMap(${namesToValues.collect { name, value -> "\"${value}\" to \"${name}\""}.join(", ")})

		fun names():Array<String> = array(${node.values.collect { "\"${it}\"" }.join(", ")})

		fun values():Array<${enumName}> = array(${node.values.collect { it }.join(", ")})

		fun getName(value:${enumName}):String = _names.get((value as Int).toString()) ?: throw IllegalArgumentException("Invalid value for ${enumName}: " + value)

		fun getValue(value:${enumName}):Int = value as Int

		fun fromValue(value:Int):${enumName} = _values.get(value.toString()) ?: throw IllegalArgumentException("Invalid value for ${enumName}: " + value)

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
		private final Map<String, Integer> namesToValues

		EnumValueVisitor(String enumName, Map<String, Integer> namesToValues) {
			this.enumName = enumName
			this.namesToValues = namesToValues
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "		val ${node.name} = ${namesToValues[node.name]} as ${enumName}"
		}
	}
}
