package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.AnnotationContext
import groovy.transform.EqualsAndHashCode
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 26/11/13.
 */
@EqualsAndHashCode
class Annotation {
	final String name
	final List<ParameterValue> parameters

	private Annotation(String name, List<ParameterValue> parameters) {
		this.name = name
		this.parameters = parameters
	}

	public List getParameterValues() {
		parameters.collect { it.value }
	}

	public static Annotation fromContext(AnnotationContext context) {
		def parameters = context.annotationParameters()?.annotationParameter()?.collect { paramCtx ->
			def name = paramCtx.name?.text
			def value = paramCtx.annotationValue().accept(new AnnotationValueExtractor())
			return new ParameterValue(name, value)
		}
		return new Annotation(context.name.text, parameters ?: [])
	}
}

@EqualsAndHashCode
class ParameterValue {
	final String name
	final def value

	ParameterValue(String name, def value) {
		this.name = name
		this.value = value
	}
}

class AnnotationValueExtractor extends ModuleBaseVisitor<Object> {
	@Override
	Object visitAnnotationNumberParameter(@NotNull @NotNull ModuleParser.AnnotationNumberParameterContext ctx)
	{
		return Double.parseDouble(ctx.numberValue.text)
	}

	@Override
	Object visitAnnotationBooleanParameter(@NotNull @NotNull ModuleParser.AnnotationBooleanParameterContext ctx)
	{
		return ctx.boolValue.text == "true"
	}

	@Override
	Object visitAnnotationStringParameter(@NotNull @NotNull ModuleParser.AnnotationStringParameterContext ctx)
	{
		return ctx.stringValue.text
	}
}
