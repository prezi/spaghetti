package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.AnnotatedNode
import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.internal.DefaultAnnotationNode
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.AnnotationValueContext

/**
 * Created by lptr on 30/05/14.
 */
class AnnotationsParser {
	static void parseAnnotations(ModuleParser.AnnotationsContext context, AnnotatedNode node) {
		context?.annotation()?.each { annotationCtx ->
			node.annotations.add fromContext(annotationCtx), annotationCtx
		}
	}

	protected static AnnotationNode fromContext(ModuleParser.AnnotationContext context) {
		def parametersContext = context.annotationParameters()
		Map<String, Object> parameters
		if (parametersContext) {
			if (parametersContext.annotationValue() != null) {
				def value = parseAnnotationValue(parametersContext.annotationValue())
				parameters = Collections.singletonMap(AnnotationNode.DEFAULT_PARAMETER, value)
			} else {
				parameters = parametersContext.annotationParameter().collectEntries() { paramCtx ->
					def name = paramCtx.Name().text
					def value = parseAnnotationValue(paramCtx.annotationValue())
					return [name, value]
				}
			}
		} else {
			parameters = Collections.emptyMap()
		}
		return new DefaultAnnotationNode(context.Name().text, parameters)
	}

	protected static Object parseAnnotationValue(AnnotationValueContext context) {
		def value
		if (context.Null()) {
			value = null
		} else if (context.Boolean()) {
			value = Primitives.parseBoolean(context.Boolean().symbol)
		} else if (context.Integer()) {
			value = Primitives.parseInt(context.Integer().symbol)
		} else if (context.Float()) {
			value = Primitives.parseDouble(context.Float().symbol)
		} else if (context.String()) {
			value = Primitives.parseString(context.String().symbol)
		} else {
			throw new InternalAstParserException(context, "Unknown annotation value")
		}
		return value
	}
}
