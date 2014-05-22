package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/05/14.
 */
class AnnotationValueExtractor extends ModuleBaseVisitor<Object> {
	@Override
	Object visitAnnotationNullParameter(@NotNull @NotNull ModuleParser.AnnotationNullParameterContext ctx) {
		return null
	}

	@Override
	Object visitAnnotationBooleanParameter(@NotNull @NotNull ModuleParser.AnnotationBooleanParameterContext ctx) {
		return Primitives.parseBoolean(ctx.boolValue)
	}

	@Override
	Object visitAnnotationIntParameter(@NotNull ModuleParser.AnnotationIntParameterContext ctx) {
		return Primitives.parseInt(ctx.intValue)
	}

	@Override
	Object visitAnnotationFloatParameter(@NotNull @NotNull ModuleParser.AnnotationFloatParameterContext ctx) {
		return Primitives.parseDouble(ctx.floatValue)
	}

	@Override
	Object visitAnnotationStringParameter(@NotNull @NotNull ModuleParser.AnnotationStringParameterContext ctx) {
		return Primitives.parseString(ctx.stringValue)
	}
}
