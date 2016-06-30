package com.prezi.spaghetti.haxe.type.enums

import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.generator.GeneratorUtils

/**
 * Generator for an enum with original definition in a direct dependency.
 *
 * For each enum member, this will generate indirections to the original enum
 * definition in the dependency.
 */
class HaxeDependentEnumGeneratorVisitor extends HaxeEnumGeneratorVisitor {
	protected final String foreignModuleName
	ModuleFormat format

	HaxeDependentEnumGeneratorVisitor(String foreignModuleName, ModuleFormat format) {
		this.foreignModuleName = foreignModuleName
		this.format = format
	}

	EnumValueVisitor createEnumValueVisitor(String enumName) {
		return new HaxeEnumGeneratorVisitor.EnumValueVisitor(enumName) {
			@Override
			String generateValueExpression(EnumValueNode node) {
				return "untyped __js__('${GeneratorUtils.createModuleAccessor(foreignModuleName, format)}[\"${enumName}\"][\"${node.name}\"]')"
			}
		}
	}
}
