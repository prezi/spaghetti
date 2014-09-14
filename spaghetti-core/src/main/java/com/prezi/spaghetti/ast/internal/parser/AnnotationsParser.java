package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Maps;
import com.prezi.spaghetti.ast.AnnotatedNode;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.internal.DefaultAnnotationNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;

import java.util.Collections;
import java.util.Map;

public class AnnotationsParser {
	public static void parseAnnotations(ModuleParser.AnnotationsContext context, AnnotatedNode node) {
		if (context != null) {
			for (ModuleParser.AnnotationContext annotationCtx : context.annotation()) {
				node.getAnnotations().add(fromContext(annotationCtx), annotationCtx);
			}
		}
	}

	protected static AnnotationNode fromContext(ModuleParser.AnnotationContext context) {
		ModuleParser.AnnotationParametersContext parametersContext = context.annotationParameters();
		Map<String, Object> parameters;
		if (parametersContext != null) {
			if (parametersContext.annotationValue() != null) {
				Object value = parseAnnotationValue(parametersContext.annotationValue());
				parameters = Collections.singletonMap(AnnotationNode.DEFAULT_PARAMETER, value);
			} else {
				parameters = Maps.newLinkedHashMap();
				for (ModuleParser.AnnotationParameterContext paramCtx : parametersContext.annotationParameter()) {
					String name = paramCtx.Name().getText();
					Object value = parseAnnotationValue(paramCtx.annotationValue());
					parameters.put(name, value);
				}
			}
		} else {
			parameters = Collections.emptyMap();
		}

		return new DefaultAnnotationNode(context.Name().getText(), parameters);
	}

	protected static Object parseAnnotationValue(ModuleParser.AnnotationValueContext context) {
		Object value;
		if (context.Null() != null) {
			value = null;
		} else if (context.Boolean() != null) {
			value = Primitives.parseBoolean(context.Boolean().getSymbol());
		} else if (context.Integer() != null) {
			value = Primitives.parseInt(context.Integer().getSymbol());
		} else if (context.Float() != null) {
			value = Primitives.parseDouble(context.Float().getSymbol());
		} else if (context.String() != null) {
			value = Primitives.parseString(context.String().getSymbol());
		} else {
			throw new InternalAstParserException(context, "Unknown annotation value");
		}

		return value;
	}
}
