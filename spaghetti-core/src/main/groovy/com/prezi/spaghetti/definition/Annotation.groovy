package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleParser.AnnotationContext
import groovy.transform.EqualsAndHashCode

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

	public boolean hasDefaultParameter()
	{
		return parameters.containsKey(DEFAULT_PARAMETER)
	}

	public def getDefaultParameter() {
		if (!hasDefaultParameter()) {
			throw new IllegalStateException("No default parameter specified for ${this}")
		}
		return parameters.get(DEFAULT_PARAMETER)
	}

	public boolean hasParameter(String parameter) {
		return parameters.containsKey(parameter)
	}

	public def getParameter(String parameter) {
		if (!hasParameter(parameter)) {
			throw new IllegalStateException("No parameter '${parameter}' specified for ${this}")
		}
		return parameters.get(parameter);
	}

	public static Annotation fromContext(AnnotationContext context) {
		def parametersContext = context.annotationParameters()
		Map<String, Object> parameters
		if (parametersContext) {
			def annotationValue = parametersContext.annotationValue()
			if (annotationValue != null) {
				def value = annotationValue.accept(new AnnotationValueExtractor())
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

	@Override
	String toString() {
		return "@${name}(${parameters.collect { key, value -> "${key} = ${value}"}.join(", ")})"
	}
}

