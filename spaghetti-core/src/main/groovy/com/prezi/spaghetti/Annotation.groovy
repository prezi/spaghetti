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
	public static final def DEFAULT_PARAMETER = "default"

	final String name
	final Map<String, Object> parameters

	private Annotation(String name, Map<String, Object> parameters) {
		this.name = name
		this.parameters = parameters
	}

	public def getDefaultParameterValue() {
		return parameters.get(DEFAULT_PARAMETER)
	}

	public static Annotation fromContext(AnnotationContext context) {
		def parametersContext = context.annotationParameters()
		Map<String, Object> parameters
		if (parametersContext) {
			if (parametersContext.singleValue != null) {
				def value = parametersContext.singleValue.accept(new AnnotationValueExtractor())
				parameters = Collections.singletonMap(DEFAULT_PARAMETER, value)
			} else {
				parameters = parametersContext.annotationParameter().collectEntries() { paramCtx ->
					def name = paramCtx.name?.text
					def value = paramCtx.annotationValue().accept(new AnnotationValueExtractor())
					return [ name, value ]
				}
			}
		} else {
			parameters = Collections.emptyMap()
		}
		return new Annotation(context.name.text, parameters)
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
