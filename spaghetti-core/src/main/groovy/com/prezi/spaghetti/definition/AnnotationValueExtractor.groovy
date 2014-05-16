package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import groovy.json.StringEscapeUtils
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
		return ctx.boolValue.text == "true"
	}

	@Override
	Object visitAnnotationNumberParameter(@NotNull @NotNull ModuleParser.AnnotationNumberParameterContext ctx) {
		return Double.parseDouble(ctx.numberValue.text)
	}

	@Override
	Object visitAnnotationStringParameter(@NotNull @NotNull ModuleParser.AnnotationStringParameterContext ctx) {
		def unescaped = StringEscapeUtils.unescapeJava(ctx.stringValue.text);
		return unescaped[1..unescaped.size() - 2] // strip surrounding quotes
	}
}
