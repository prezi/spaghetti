package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class KotlinEnumGeneratorVisitor extends AbstractKotlinGeneratorVisitor {
	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name

		return \
"""class ${enumName} {
	companion object {
${node.values*.accept(new EnumValueVisitor(node.name)).join("\n")}
		private val _values = hashMapOf(${node.values.collect { entry -> "${entry.name}.toString() to ${entry.name}"}.join(", ")})
		private val _names = hashMapOf(${node.values.collect { entry -> "${entry.name}.toString() to \"${entry.name}\""}.join(", ")})

		fun names():Array<String> = arrayOf(${node.values.collect { "\"${it}\"" }.join(", ")})

		fun values():Array<${enumName}> = arrayOf(${node.values.collect { it }.join(", ")})

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

		EnumValueVisitor(String enumName) {
			this.enumName = enumName
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "		val ${node.name} = ${node.value} as ${enumName}"
		}
	}
}
