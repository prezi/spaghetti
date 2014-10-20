package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name
		def valueLines = []
		node.values.eachWithIndex{ value, index ->
			valueLines += value.accept(new TypeScriptEnumValueGeneratorVisitor(enumName, index))
		}
"""export class ${enumName} {
${valueLines.join("\n")}

	private static _values:Array<${enumName}> = [ ${node.values.collect { "${enumName}.${it}" }.join(", ")} ];
	private static _names:Array<string> =  [ ${node.values.collect { "\"${it}\"" }.join(", ")} ];

	static names():Array<string> {
		return ${enumName}._names.slice(0);
	}

	static values():Array<${enumName}> {
		return ${enumName}._values.slice(0);
	}

	static name(value:${enumName}) {
		return ${enumName}._names[<number> value];
	}

	static value(value:${enumName}):number {
		return <number> value;
	}

	static fromValue(value:number):${enumName} {
		if (value < 0 || value >= ${enumName}._values.length) {
			throw "Invalid value for ${enumName}: " + value;
		}
		return ${enumName}._values[value];
	}

	static valueOf(name:String):${enumName} {
		var result:${enumName};
		switch(name)
		{
${node.values.collect { "			case \"${it}\": result = ${enumName}.${it}; break;" }.join("\n")}
			default: throw Error("Invalid name for ${enumName}: " + name);
		};
		return result;
	}
}
"""
	}

	private static class TypeScriptEnumValueGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
		private final String enumName
		private final int valueIndex

		TypeScriptEnumValueGeneratorVisitor(String enumName, int valueIndex) {
			this.enumName = enumName
			this.valueIndex = valueIndex
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\tstatic ${node.name}:${enumName} = ${valueIndex};"
		}
	}
}
